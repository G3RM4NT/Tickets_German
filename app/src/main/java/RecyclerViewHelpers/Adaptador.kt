package RecyclerViewHelper

import Modelo.ClaseConexion
import Modelo.Ticket
import android.text.Layout

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.livedata.core.R
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.UUID


class Adaptador(private var Datos: List<Ticket>) : RecyclerView.Adapter<ViewHolder>() {


    fun actualizarLista(nuevaLista: List<Ticket>) {
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun actualicePantalla(
        Titulo: String,
        Descripcion: String,
        Autor: String,
        Autoremail: String,
        Creationdate: String,
        TicketStatus: String,
        Finishdate: String,
        UUID_Tickets: String
    ) {
        val index = Datos.indexOfFirst { it.UUID_Tickets == UUID_Tickets }
        Datos[index].Titulo = Titulo
        Datos[index].Descripcion = Descripcion
        Datos[index].Autor = Autor
        Datos[index].AutorEmail = Autoremail
        Datos[index].AutorEmail = Creationdate
        Datos[index].TicketStatus = TicketStatus
        Datos[index].Finishdate = Finishdate

        notifyDataSetChanged()
    }


    fun eliminarDatos(titulo: String, posicion: Int) {

        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            //1- Creamos un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Crear una variable que contenga un PrepareStatement
            val deleteTicket =
                objConexion?.prepareStatement("delete from Tickets where Titulo = ?")!!
            deleteTicket.setString(1, titulo)
            deleteTicket.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()

        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }


    fun actualizarDato(
        Titulo: String,
        Descripcion: String,
        Autor: String,
        Autoremail: String,
        Creationdate: String,
        TicketStatus: String,
        Finishdate: String,
        UUID_Tickets: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {

            //1- Creo un objeto de la clase de conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- creo una variable que contenga un PrepareStatement
            val addTicket =
                objConexion?.prepareStatement("UPDATE Tickets SET Titulo = ?, Descripcion = ?, Autor = ?, AutorEmail = ?, CreationDate = ?, TicketStatus = ?,FinishDate = ?  WHERE UUID_Tickets = ?")!!
            addTicket.setString(1, Titulo)
            addTicket.setString(2, Descripcion)
            addTicket.setString(3, Autor)
            addTicket.setString(4, Autoremail)
            addTicket.setString(5, Creationdate)
            addTicket.setString(6, TicketStatus)
            addTicket.setString(7, Finishdate)
            addTicket.setString(8, UUID_Tickets)

            withContext(Dispatchers.Main) {
                actualicePantalla(
                    Titulo,
                    Descripcion,
                    Autor,
                    Autoremail,
                    Creationdate,
                    TicketStatus,
                    Finishdate,
                    UUID_Tickets
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")

val vista =
LayoutInflater.from(parent.context).inflate()
    }
    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Ticket = Datos[position]
        holder.lb = Ticket.Titulo
        holder.l.text = Ticket.Descripcion
        holder.lbautorDeTicket.text = Ticket.Autor
        holder.lbemailDeAutor.text = Ticket.AutorEmail
        holder.lbfechaDeCreacionDeTicket.text = Ticket.CreationDate
        holder.lbestadoDeTicket.text = Ticket.TicketStatus
        holder.lbfechaDeFinalizacionDeTicket.text = Ticket.Finishdate

        holder.imgEliminar.setOnClickListener{

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar la mascota?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(Ticket.Titulo, position)
            }

            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        //Todo: icono de editar
        holder.imgEditar.setOnClickListener{
            val context = holder.itemView.context

            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            val txt1 = EditText(context)
            layout.addView(txt1)
            txt1.setText(Ticket.Titulo)
            val txt2 = EditText(context)
            layout.addView(txt2)
            txt2.setText(Ticket.Descripcion)
            val txt3 = EditText(context)
            layout.addView(txt3)
            txt3.setText(Ticket.Autor)
            val txt4 = EditText(context)
            layout.addView(txt4)
            txt4.setText(Ticket.AutorEmail)
            val txt5 = EditText(context)
            txt5.setText(Ticket.TicketStatus)
            layout.addView(txt5)
            val txt6 = EditText(context)
            txt6.setText(Ticket.Finishdate)
            layout.addView(txt6)

            val Uuid = Ticket.UUID_Tickets

            val builder = AlertDialog.Builder(context)
            builder.setView(layout)
            builder.setTitle("Editar Ticket")


            builder.setPositiveButton("Aceptar") { dialog, which ->
                actualizarDato(txt1.text.toString(),txt2.text.toString(),txt3.text.toString(),txt4.text.toString(),txt5.text.toString(),txt6.text.toString(),Uuid)
                Toast.makeText(context, "Ticket editado correctamente", Toast.LENGTH_SHORT).show()

            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }



}
