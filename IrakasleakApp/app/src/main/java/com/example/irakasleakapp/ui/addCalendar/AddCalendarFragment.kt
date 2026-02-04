package com.example.irakasleakapp.ui.addCalendar

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.irakasleakapp.NavigationAcitvity
import com.example.irakasleakapp.data.Metting
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.data.api.model.ReunionPostRequest

import com.example.irakasleakapp.databinding.FragmentAddCalendarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddCalendarFragment : Fragment() {

    private var _binding: FragmentAddCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddCalendarViewModel by viewModels()
    private val dbFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val horasLabel = arrayOf("08:00", "09:00", "10:00", "11:00", "11:30", "12:30", "13:30")
    private var listaProfesores = listOf<Person>()

    lateinit var useractual : Person
    private val ALTURA_FILA_DP = 85
    private val ALTURA_CABECERA_DP = 40
    private val ANCHO_HORA_DP = 60

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inicializarGrid()

        val user = (activity as? NavigationAcitvity)?.user
        user?.let {
            viewModel.getReuniones(it.id)
            viewModel.getUsersSearch()
        }

        binding.btnIconoTabla.setOnClickListener { abrirDialogoNuevaCita() }

        // Listener para cerrar el popup si se toca el fondo oscuro
        binding.viewOscurecer.setOnClickListener {
            cerrarPopup()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                useractual = obtenerUsuario()!!


                launch {
                    viewModel.reunionesState.collect { lista ->
                        activity?.runOnUiThread {
                            inicializarGrid()
                            lista.forEach { reunion ->
                                agregarReunionAlGrid(reunion)
                            }
                        }
                    }
                }
                launch {
                    viewModel.userListState.collect { lista ->
                        if (lista != null) listaProfesores = lista
                    }
                }
            }
        }
    }

    private fun agregarReunionAlGrid(reunion: Metting) {
        try {
            val fechaLimpia = reunion.fecha.split(".")[0]
            val fecha = dbFormatter.parse(fechaLimpia) ?: return
            val cal = Calendar.getInstance().apply { time = fecha }

            // Verifica que el mes sea Enero (0) y el año 2026 para que coincida con tu grid
            val diaMes = cal.get(Calendar.DAY_OF_MONTH)
            val col = when(diaMes) {
                12 -> 1; 13 -> 2; 14 -> 3; 15 -> 4; 16 -> 5; else -> -1
            }

            val h = cal.get(Calendar.HOUR_OF_DAY)
            val m = cal.get(Calendar.MINUTE)

            val row = when {
                h == 8 -> 1
                h == 9 -> 2
                h == 10 -> 3
                h == 11 && m >= 30 -> 5
                h == 12 && m >= 30 -> 6
                h == 13 && m >= 30 -> 7
                else -> -1
            }

            if (row != -1 && col != -1) {
                // Ya no necesitamos runOnUiThread aquí porque ya estamos dentro en el observer
                val card = CardView(requireContext()).apply {
                    tag = "reunion_dinamica"
                    radius = 8f
                    cardElevation = 4f

                    // Definir colores según el estado
                    val (colorFondo, colorTexto) = when (reunion.estado.lowercase()) {
                        "aceptada" -> {
                            // Verde
                            "#E8F5E9" to "#2E7D32"
                        }
                        "denegada" -> {
                            // Rojo
                            "#FFEBEE" to "#B71C1C"
                        }
                        else -> {
                            // Amarillo (para estados pendientes o cualquier otro)
                            "#FFFDE7" to "#FBC02D"
                        }
                    }

                    setCardBackgroundColor(Color.parseColor(colorFondo))

                    addView(TextView(context).apply {
                        text = "(${reunion.estado})"
                        textSize = 9f
                        setTextColor(Color.parseColor(colorTexto))
                        gravity = Gravity.CENTER
                        setPadding(4, 4, 4, 4)
                        setTypeface(null, Typeface.BOLD) // Opcional: negrita para que se vea mejor
                    })

                    setOnClickListener { mostrarpopup(reunion) }
                }

                val params = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col, 1f)).apply {
                    width = 0
                    height = dpToPx(ALTURA_FILA_DP - 10)
                    setMargins(6, 6, 6, 6)
                    setGravity(Gravity.FILL)
                }

                binding.gridHorario.addView(card, params)
            }
        } catch (e: Exception) {
            Log.e("GridError", "Error al pintar reunión ${reunion.id}: ${e.message}")
        }
    }

    private fun mostrarpopup(reunion: Metting) {
        if(useractual.tipos.id == 4){
            Toast.makeText(requireContext(), "No tienes permisos para usar esta función", Toast.LENGTH_SHORT).show()
        }else {
            binding.poup.visibility = View.VISIBLE
            binding.viewOscurecer.visibility = View.VISIBLE

            binding.btnConfirmar.setOnClickListener {
                aceptarDenegarReuniones(reunion)
            }
        }

    }
    private fun aceptarDenegarReuniones(reunion: Metting) {
        val body = when {
            binding.rbAceptada.isChecked -> mapOf(
                "idReunion" to reunion.id.toString(),
                "nuevoEstado" to "aceptada"
            )
            binding.rbDenegada.isChecked -> mapOf(
                "idReunion" to reunion.id.toString(),
                "nuevoEstado" to "denegada"
            )
            else -> {
                Toast.makeText(requireContext(), "Seleccione una opción", Toast.LENGTH_SHORT).show()
                return
            }
        }

        Log.d("javi72", "Body: $body")

        val user = (activity as? NavigationAcitvity)?.user
        user?.let {
            viewModel.updateReunion(body, it.id) // ✅ Pasar el userId
        }

        cerrarPopup()
    }
    private fun cerrarPopup() {
        binding.poup.visibility = View.GONE
        binding.viewOscurecer.visibility = View.GONE
    }

    private fun inicializarGrid() {
        binding.gridHorario.removeAllViews()
        val diasNom = arrayOf("", "LUN", "MAR", "MIE", "JUE", "VIE")

        diasNom.forEachIndexed { i, d ->
            val tv = TextView(context).apply {
                text = d; gravity = Gravity.CENTER; textSize = 12f; setTypeface(null, Typeface.BOLD)
                layoutParams = GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(i, 1f)).apply {
                    width = 0; height = dpToPx(ALTURA_CABECERA_DP)
                }
            }
            binding.gridHorario.addView(tv)
        }

        horasLabel.forEachIndexed { index, h ->
            val row = index + 1
            binding.gridHorario.addView(TextView(context).apply {
                text = h; gravity = Gravity.CENTER; textSize = 10f
                layoutParams = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(0)).apply {
                    width = dpToPx(ANCHO_HORA_DP); height = dpToPx(ALTURA_FILA_DP)
                }
            })

            if (h == "11:00") {
                val pCard = CardView(requireContext()).apply {
                    tag = "patio"; setCardBackgroundColor(Color.parseColor("#F3E5F5")); radius = 8f
                    addView(TextView(context).apply { text = "RECREO"; gravity = Gravity.CENTER; setTextColor(Color.parseColor("#9C27B0")); textSize = 10f })
                }
                val params = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(1, 5)).apply {
                    width = 0; height = dpToPx(35); setMargins(8, 12, 8, 12); setGravity(Gravity.FILL)
                }
                binding.gridHorario.addView(pCard, params)
            } else {
                for (c in 1..5) {
                    val v = View(context).apply { layoutParams = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(c, 1f)).apply { width = 0; height = dpToPx(ALTURA_FILA_DP) } }
                    binding.gridHorario.addView(v)
                }
            }
        }
    }

    private fun abrirDialogoNuevaCita() {
        val dias = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        val horasValidas = arrayOf("08:00", "09:00", "10:00", "11:30", "12:30", "13:30")

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 20)
        }

        val spDia = Spinner(context).apply { adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, dias) }
        val spHora = Spinner(context).apply { adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, horasValidas) }
        val nombresProfes = listaProfesores.map { it.username ?: "Usuario sin nombre" }
        val spProfe = Spinner(context).apply { adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombresProfes) }
        val etAsunto = EditText(context).apply { hint = "Asunto" }

        layout.addView(TextView(context).apply { text = "Profesor:" })
        layout.addView(spProfe)
        layout.addView(TextView(context).apply { text = "Día:" })
        layout.addView(spDia)
        layout.addView(TextView(context).apply { text = "Hora:" })
        layout.addView(spHora)
        layout.addView(TextView(context).apply { text = "Asunto:" })
        layout.addView(etAsunto)

        AlertDialog.Builder(requireContext())
            .setTitle("Nueva Cita")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                if (listaProfesores.isEmpty()) return@setPositiveButton
                val cal = Calendar.getInstance().apply {
                    set(2026, Calendar.JANUARY, 12 + spDia.selectedItemPosition, 0, 0, 0)
                    val hStr = horasValidas[spHora.selectedItemPosition]
                    set(Calendar.HOUR_OF_DAY, hStr.split(":")[0].toInt())
                    set(Calendar.MINUTE, hStr.split(":")[1].toInt())
                }
                val userLogueado = (activity as? NavigationAcitvity)?.user
                val request = ReunionPostRequest(
                    titulo = etAsunto.text.toString(),
                    asunto = etAsunto.text.toString(),
                    aula = 0,
                    fecha = dbFormatter.format(cal.time),
                    alumnoId = userLogueado?.id ?: 1,
                    profesorId = listaProfesores[spProfe.selectedItemPosition].id
                )
                viewModel.crearNuevaReunion(request)
                inicializarGrid()

            }.show()
        inicializarGrid()
    }

    private fun removerTarjetasDinamicas() {
        val toRemove = mutableListOf<View>()
        for (i in 0 until binding.gridHorario.childCount) {
            val v = binding.gridHorario.getChildAt(i)
            if (v.tag == "reunion_dinamica") toRemove.add(v)
        }
        toRemove.forEach { binding.gridHorario.removeView(it) }
    }

    private fun dpToPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
    ).toInt()

    override fun onDestroyView() { super.onDestroyView(); _binding = null }



    private fun obtenerUsuario(): Person? {
        val activity = requireActivity() as NavigationAcitvity
        return activity.user
    }
}