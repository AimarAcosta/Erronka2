package com.example.irakasleakapp.ui.searchUsers.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.irakasleakapp.R
import com.example.irakasleakapp.data.Person

class AdaptadorDeUsuario(
    var listaPersonas: ArrayList<Person>,
    private val onClick: (Person) -> Unit
) : RecyclerView.Adapter<AdaptadorDeUsuario.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvCiclo: TextView = itemView.findViewById(R.id.tvCiclo)
        val tvInitials: TextView = itemView.findViewById(R.id.tvInitials)
        val phoneLayout: View = itemView.findViewById(R.id.phoneLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_usuarios, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val persona = listaPersonas[position]

        // Nombre
        holder.tvNombre.text = persona.username ?: "Usuario"

        // Iniciales para el avatar
        val initials = getInitials(persona.username ?: "?")
        holder.tvInitials.text = initials

        // Ciclo y Curso combinados
        val cicloInfo = buildString {
            if (!persona.ciclo.isNullOrEmpty()) {
                append(persona.ciclo)
            }
            if (!persona.curso.isNullOrEmpty()) {
                if (isNotEmpty()) append(" • ")
                append(persona.curso)
            }
        }
        holder.tvCiclo.text = cicloInfo.ifEmpty { "Sin información" }

        // Email
        holder.tvEmail.text = persona.email ?: "Sin email"

        // Teléfono (mostrar/ocultar layout si no hay teléfono)
        if (!persona.telefono.isNullOrEmpty()) {
            holder.tvTelefono.text = persona.telefono
            holder.phoneLayout.visibility = View.VISIBLE
        } else {
            holder.phoneLayout.visibility = View.GONE
        }

        // Click listener
        holder.itemView.setOnClickListener {
            onClick(persona)
        }
    }

    override fun getItemCount(): Int = listaPersonas.size

    fun filtrar(listaFiltrada: ArrayList<Person>) {
        this.listaPersonas = listaFiltrada
        notifyDataSetChanged()
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 && parts[0].isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "?"
        }
    }
}