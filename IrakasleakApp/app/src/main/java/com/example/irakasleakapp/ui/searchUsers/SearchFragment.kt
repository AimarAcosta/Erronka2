package com.example.irakasleakapp.ui.searchUsers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.irakasleakapp.NavigationAcitvity
import com.example.irakasleakapp.R
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.databinding.FragmentSearchBinding
import com.example.irakasleakapp.ui.searchUsers.Adapter.AdaptadorDeUsuario
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var useractual: Person
    private val viewModel: SearchfragmentViewModel by viewModels()
    private lateinit var adapter: AdaptadorDeUsuario

    private var listaPersonas = arrayListOf<Person>()
    private var cicloSeleccionado = "Todos"
    private var cursoSeleccionado = "Todos"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos el usuario actual de forma segura
        obtenerUsuario()?.let { useractual = it }


        if(useractual.tipos.id == 4){

            binding.spinnerCurso.visibility = View.GONE
            binding.spinnersLayout.visibility = View.GONE
            binding.spinnerCiclo.visibility = View.GONE
        }



        setupRecyclerView(useractual)
        initListener()
        setupSearchListener()
        llenarLista()
    }

    private fun initListener() {
        binding.iconConfiguration.setOnClickListener {
            val options = navOptions {
                popUpTo(R.id.searchFragment) { inclusive = true }
            }
            findNavController().navigate(R.id.action_searchFragment_to_settingsFragment, null, options)
        }
    }

    private fun setupSearchListener() {
        binding.etBuscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { aplicarFiltros() }
        })
    }

    private fun configurarSpinners() {
        // Ciclos
        val listaCiclos = listaPersonas.mapNotNull { it.ciclo }.distinct().sorted().toMutableList()
        listaCiclos.add(0, "Todos")
        binding.spinnerCiclo.setSimpleItems(listaCiclos)

        // Cursos
        val listaCursos = listaPersonas.mapNotNull { it.curso }.distinct().sorted().toMutableList()
        listaCursos.add(0, "Todos")
        binding.spinnerCurso.setSimpleItems(listaCursos)

        // Listeners
        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Actualizamos las variables globales según qué spinner se movió
                if (parent?.id == R.id.spinnerCiclo) cicloSeleccionado = listaCiclos[position]
                if (parent?.id == R.id.spinnerCurso) cursoSeleccionado = listaCursos[position]
                aplicarFiltros()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerCiclo.onItemSelectedListener = listener
        binding.spinnerCurso.onItemSelectedListener = listener
    }

    private fun llenarLista() {
        viewModel.getUsersSearch()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userListState.collect { listaApi ->
                    listaApi?.let {
                        listaPersonas.clear()
                        listaPersonas.addAll(it)
                        configurarSpinners()
                        aplicarFiltros() // Importante: Filtrar nada más recibir
                    }
                }
            }
        }
    }




    private fun Spinner.setSimpleItems(items: List<String>) {
        val adapterSpinner = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, items)
        this.adapter = adapterSpinner
    }

    private fun setupRecyclerView(useractual: Person) {
        binding.rvLista.layoutManager = LinearLayoutManager(requireContext())

        // Inicializamos con lista vacía, se llenará en aplicarFiltros()
        adapter = AdaptadorDeUsuario(arrayListOf()) { user ->
            // 1. Log de diagnóstico

            Log.d("Navegacion", "Usuario: ${user.username} | Tipo ID detectado: ${useractual.tipos.id}")

            val bundle = Bundle().apply {
                putString("username", user.username)
            }

            // 2. Verificamos la navegación
            when (useractual.tipos.id) {
                4 -> {
                    Log.d("Navegacion", "Navegando a Settings")
                    findNavController().navigate(
                        R.id.settingsFragment, // Asegúrate de que este ID sea el correcto en tu nav_graph
                        bundle
                    )

                }
                1, 2, 3 -> {
                    Log.d("Navegacion", "Navegando a Detail")
                    findNavController().navigate(
                        R.id.action_searchFragment_to_searchDetailFragment,
                        bundle
                    )


                }
                else -> {
                    Log.e("Navegacion", "ID no reconocido: ${useractual.tipos.id}")
                }
            }
        }
        binding.rvLista.adapter = adapter
    }

    private fun aplicarFiltros() {
        val textoBusqueda = binding.etBuscador.text.toString()

        val listaFiltrada = listaPersonas.filter { persona ->
            val coincideTexto = textoBusqueda.isEmpty() ||
                    (persona.username?.contains(textoBusqueda, ignoreCase = true) == true) ||
                    (persona.email?.contains(textoBusqueda, ignoreCase = true) == true)

            val coincideCiclo = cicloSeleccionado == "Todos" || persona.ciclo == cicloSeleccionado
            val coincideCurso = cursoSeleccionado == "Todos" || persona.curso == cursoSeleccionado

            coincideTexto && coincideCiclo && coincideCurso
        }

        // Enviamos la lista al adaptador
        adapter.filtrar(ArrayList(listaFiltrada))
        actualizarContador(listaFiltrada.size)

        // UI Feedback
        binding.emptyState.visibility = if (listaFiltrada.isEmpty()) View.VISIBLE else View.GONE
        binding.rvLista.visibility = if (listaFiltrada.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun actualizarContador(count: Int) {
        binding.tvResultsCount.text = when (count) {
            0 -> "No results"
            1 -> "1 result"
            else -> "$count results"
        }
    }

    private fun obtenerUsuario(): Person? {
        return (activity as? NavigationAcitvity)?.user
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}