package com.example.irakasleakapp.ui.settings

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.irakasleakapp.NavigationAcitvity
import com.example.irakasleakapp.R
import com.example.irakasleakapp.data.Person
import com.example.irakasleakapp.databinding.FragmentSettingsBinding
import com.example.irakasleakapp.ui.home.HomeLogin
import dagger.hilt.android.AndroidEntryPoint
import java.io.OutputStream

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // 1. Launcher para la CÁMARA
    private val tomarFotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                cargarImagenCircular(it)
                guardarEnGaleria(it)
            }
        }
    }

    // 2. Launcher para la GALERÍA
    private val seleccionarGaleriaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            cargarImagenCircularDesdeUri(it)
            Toast.makeText(requireContext(), "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarUsuario()

        // Al hacer clic en la FOTO de perfil
        binding.imgPerfil.setOnClickListener {
            mostrarOpcionesImagen()
        }

        // Botón de cámara flotante
        binding.btnAbrirCamara.setOnClickListener {
            mostrarOpcionesImagen()
        }

        // Botón de Logout
        binding.btnSaveChanges.setOnClickListener {
            salirAlLogin()
        }


    }

    // DIÁLOGO DE SELECCIÓN
    private fun mostrarOpcionesImagen() {
        val opciones = arrayOf("Hacer foto", "Elegir de galería", "Cancelar")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cambiar foto de perfil")
        builder.setItems(opciones) { dialog, i ->
            when (i) {
                0 -> comprobarPermisosYCamara()
                1 -> seleccionarGaleriaLauncher.launch("image/*")
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun comprobarPermisosYCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            abrirCamara()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        }
    }

    private fun configurarUsuario() {
        try {
            val usuario = obtenerUsuario()
            if (usuario != null) {
                // Cargar datos del usuario
                binding.tvName.text = usuario.username ?: "Usuario"
                binding.tvUsername.text = usuario.username ?: "N/A"
                binding.tvEmail.text = usuario.email ?: "N/A"

                // Dividir el nombre completo si existe
                val nombreCompleto = usuario.username?.split(" ")
                if (!nombreCompleto.isNullOrEmpty()) {
                    binding.tvFirstName.text = nombreCompleto.getOrNull(0) ?: "N/A"
                    binding.tvSurname.text = nombreCompleto.drop(1).joinToString(" ").ifEmpty { "N/A" }
                } else {
                    binding.tvFirstName.text = "N/A"
                    binding.tvSurname.text = "N/A"
                }

                // Cargar teléfono si existe en tu modelo Person
                binding.tvPhone.text = usuario.telefono ?: "N/A"

                Log.d("SettingsFragment", "Configurando usuario: ${usuario.argazkiaUrl}")

                // Cargar imagen de perfil circular
                if (!usuario.argazkiaUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(usuario.argazkiaUrl)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.icon_configuration)
                                .error(R.drawable.icon_configuration)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                        )
                        .into(binding.imgPerfil)
                } else {
                    binding.imgPerfil.setImageResource(R.drawable.icon_configuration)
                }
            } else {
                mostrarDatosVacios()
            }
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error al configurar usuario: ${e.message}")
            mostrarDatosVacios()
        }
    }

    private fun mostrarDatosVacios() {
        binding.tvName.text = "Usuario no disponible"
        binding.tvUsername.text = "N/A"
        binding.tvEmail.text = "N/A"
        binding.tvFirstName.text = "N/A"
        binding.tvSurname.text = "N/A"
        binding.tvPhone.text = "N/A"
        binding.imgPerfil.setImageResource(R.drawable.icon_configuration)
    }

    private fun cargarImagenCircular(bitmap: Bitmap) {
        Glide.with(this)
            .load(bitmap)
            .apply(RequestOptions().centerCrop())
            .into(binding.imgPerfil)
    }

    private fun cargarImagenCircularDesdeUri(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .apply(RequestOptions().centerCrop())
            .into(binding.imgPerfil)
    }

    private fun guardarEnGaleria(bitmap: Bitmap) {
        val nombreArchivo = "Perfil_${System.currentTimeMillis()}.jpg"
        var outputStream: OutputStream? = null
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/IrakasleakApp")
        }
        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            uri?.let {
                outputStream = resolver.openOutputStream(it)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
                Toast.makeText(requireContext(), "¡Foto guardada!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Fallo al guardar: ${e.message}")
            Toast.makeText(requireContext(), "Error al guardar la foto", Toast.LENGTH_SHORT).show()
        } finally {
            outputStream?.close()
        }
    }

    private fun abrirCamara() {
        val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        tomarFotoLauncher.launch(intentCamara)
    }

    private fun obtenerUsuario(): Person? {
        return (requireActivity() as? NavigationAcitvity)?.user
    }

    private fun salirAlLogin() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                val intent = Intent(requireContext(), HomeLogin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}