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

        val vista =
            LayoutInflater.from(parent.context).inflate(
            German.Gonzalez.germanantoniogonzalezmejiatickets.R.layout.activity_itemcard,
            parent,
            false
        )


        return RecyclerViewHelper.ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = Datos.size

    over fun onBindViewHolder(holder: RecyclerViewHelper.ViewHolder, position: Int) {
        val ticket = Datos[position]
        holder.lbtituloDeTicket.text = ticket.Titulo
        holder.lbdescripcionDeTicket.text = ticket.Descripcion
        holder.lbautorDeTicket.text = ticket.Autor
        holder.lbemailDeAutor.text = ticket.AutorEmail
        holder.lbfechaDeCreacionDeTicket.text = ticket.CreationDate
        holder.lbestadoDeTicket.text = ticket.TicketStatus
        holder.lbfechaDeFinalizacionDeTicket.text = ticket.Finishdate

        holder.imgEliminar.setOnClickListener{

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar la mascota?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(ticket.Titulo, position)
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
            txt1.setText(ticket.Titulo)
            val txt2 = EditText(context)
            layout.addView(txt2)
            txt2.setText(ticket.Descripcion)
            val txt3 = EditText(context)
            layout.addView(txt3)
            txt3.setText(ticket.Autor)
            val txt4 = EditText(context)
            layout.addView(txt4)
            txt4.setText(ticket.AutorEmail)
            val txt5 = EditText(context)
            txt5.setText(ticket.TicketStatus)
            layout.addView(txt5)
            val txt6 = EditText(context)
            txt6.setText(ticket.Finishdate)
            layout.addView(txt6)

            val uuid = ticket.UUID_Tickets

            val builder = AlertDialog.Builder(context)
            builder.setView(layout)
            builder.setTitle("Editar Ticket")


            builder.setPositiveButton("Aceptar") { dialog, which ->
                actualizarDato(txt1.text.toString(),txt2.text.toString(),txt3.text.toString(),txt4.text.toString(),txt5.text.toString(),txt6.text.toString(),uuid)
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

