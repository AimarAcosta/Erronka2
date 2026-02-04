package com.example.irakasleakapp.ui.calendar

import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.irakasleakapp.NavigationAcitvity
import com.example.irakasleakapp.R
import com.example.irakasleakapp.data.Horarios
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.databinding.FragmentCalendarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val bi get() = _binding!!

    private lateinit var soundPool: SoundPool
    private var sonidoSecreto: Int = 0

    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return bi.root
    }

    private fun obtenerUsuario(): Person? {
        val activity = requireActivity() as NavigationAcitvity
        return activity.user
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Cargar datos del usuario
        obtenerUsuario()?.let { usuario ->
            Log.d("javi", "Llamando API con username: ${usuario.username}")
            viewModel.getHorarios(usuario.username)
        } ?: Log.e("javi", "No se pudo obtener el usuario")

        // 2. Escuchar cambios en el StateFlow (UI dinámica)
        observeViewModel()

        // 3. Inicializar extras (Sonidos y Clics)
        setupUI()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.horariosState.collectLatest { lista ->
                    if (lista.isNotEmpty()) {
                        Log.d("javi", "Datos recibidos con éxito. Pintando tabla...")
                        mostrarHorariosEnTabla(lista)
                    } else {
                        Log.d("javi", "La lista sigue vacía o esperando respuesta...")
                    }
                }
            }
        }
    }

    private fun setupUI() {
        soundPool = SoundPool.Builder().setMaxStreams(1).build()
        sonidoSecreto = soundPool.load(requireContext(), R.raw.fnaf, 1)

        bi.btnCerrar.setOnClickListener { ocultarPopup() }
        bi.viewDim.setOnClickListener { ocultarPopup() }
    }

    // Mapea el texto del API al índice de columna del XML
    private fun diaAInt(dia: String): Int {
        return when (dia.uppercase()) {
            "LUNES" -> 0
            "MARTES" -> 1
            "MIERCOLES", "MIÉRCOLES" -> 2
            "JUEVES" -> 3
            "VIERNES" -> 4
            else -> -1
        }
    }

    private fun mostrarHorariosEnTabla(horarios: List<Horarios>) {
        for (horario in horarios) {
            val diaNum = diaAInt(horario.dia)

            // Construimos el ID dinámicamente: slot_HORA_DIA (ej: slot_0_0)
            val celdaId = resources.getIdentifier(
                "slot_${horario.hora}_${diaNum}",
                "id",
                requireContext().packageName
            )

            val celda = bi.gridHorario.findViewById<TextView>(celdaId)

            if (celda != null) {
                celda.text = horario.modulo
                celda.setOnClickListener {
                    mostrarPopup(
                        materia = "${horario.modulo}\nAula: ${horario.aula}",
                        hora = horaToString(horario.hora),
                        mostrarImagen = false
                    )
                }
            } else {
                Log.e("javi", "Error: No existe el ID slot_${horario.hora}_${diaNum} en el XML")
            }
        }

        // Slot secreto pascua
        bi.gridHorario.getChildAt(0)?.setOnClickListener {
            reproducirSonido()
            mostrarPopup("🎉 Sorpresa", "Has encontrado el secreto", true)
            Glide.with(this).asGif().load(R.drawable.foxygif).into(bi.ivImagenPopUp)
        }
    }

    private fun horaToString(horaIndex: Int): String {
        return when (horaIndex) {
            0 -> "08:00 - 09:00"
            1 -> "09:00 - 10:00"
            2 -> "10:00 - 11:00"
            3 -> "11:30 - 12:30"
            4 -> "12:30 - 13:30"
            5 -> "13:30 - 14:30"
            else -> "Hora especial"
        }
    }

    private fun reproducirSonido() {
        soundPool.play(sonidoSecreto, 1f, 1f, 1, 0, 1f)
    }

    private fun mostrarPopup(materia: String, hora: String, mostrarImagen: Boolean) {
        bi.tvMateriaDetalle.text = materia
        bi.tvHoraDetalle.text = hora
        bi.ivImagenPopUp.visibility = if (mostrarImagen) View.VISIBLE else View.GONE
        bi.viewDim.visibility = View.VISIBLE
        bi.panelDetalles.apply {
            visibility = View.VISIBLE
            alpha = 0f
            scaleX = 0.7f
            scaleY = 0.7f
            animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(250).start()
        }
    }

    private fun ocultarPopup() {
        bi.panelDetalles.animate().alpha(0f).scaleX(0.7f).scaleY(0.7f).setDuration(200)
            .withEndAction {
                bi.panelDetalles.visibility = View.GONE
                bi.viewDim.visibility = View.GONE
            }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool.release()
        _binding = null
    }
}