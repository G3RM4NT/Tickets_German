package RecyclerViewHelper

import RecyclerViewHelpers.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import enrique.chavarria.crud_enriquechavarra.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Tickets
import java.util.UUID


class Adaptador(private var Datos: List<Tickets>) : RecyclerView.Adapter<ViewHolder>() {



    fun actualizarLista(nuevaLista: List<Tickets>) {
        Datos = nuevaLista
        notifyDataSetChanged()
    }

    fun actualicePantalla(titulo: String, descripcion: String, autor: String, emailAutor: String, estadoTicket:String, fechaFinalizacion:String, uuid:String){
        val index = Datos.indexOfFirst { it.uuid == uuid}
        Datos[index].tituloDeTicket = titulo
        Datos[index].descripcionDeTicket = descripcion
        Datos[index].autorDeTicket = autor
        Datos[index].emailDeAutor = emailAutor
        Datos[index].estadoDeTicket = estadoTicket
        Datos[index].fechaDeFinalizacionDeTicket = fechaFinalizacion

        notifyDataSetChanged()
    }




    fun eliminarDatos(titulo: String, posicion: Int){

        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO){
            //1- Creamos un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Crear una variable que contenga un PrepareStatement
            val deleteTicket = objConexion?.prepareStatement("delete from TB_Ticket where tituloDeTicket = ?")!!
            deleteTicket.setString(1, titulo)
            deleteTicket.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()

        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }


    fun actualizarDato(titulo: String, descripcion: String, autor: String, emailAutor: String, estadoTicket:String, fechaFinalizacion:String, uuid: String){
        GlobalScope.launch(Dispatchers.IO){

            //1- Creo un objeto de la clase de conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- creo una variable que contenga un PrepareStatement
            val addTicket = objConexion?.prepareStatement("UPDATE tb_ticket SET tituloDeticket = ?, descripcionDeTicket = ?, autorDeTicket = ?, emailDeAutor = ?, estadoDeTicket = ?, fechaDeFinalizacionDeTicket = ? WHERE UUID = ?")!!
            addTicket.setString(1, titulo)
            addTicket.setString(2, descripcion)
            addTicket.setString(3, autor)
            addTicket.setString(4, emailAutor)
            addTicket.setString(5, estadoTicket)
            addTicket.setString(6, fechaFinalizacion)
            addTicket.setString(7, uuid)

            withContext(Dispatchers.Main){
                actualicePantalla(titulo, descripcion, autor, emailAutor, estadoTicket, fechaFinalizacion, uuid)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)

        return ViewHolder(vista)
    }
    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = Datos[position]
        holder.lbtituloDeTicket.text = ticket.tituloDeTicket
        holder.lbdescripcionDeTicket.text = ticket.descripcionDeTicket
        holder.lbautorDeTicket.text = ticket.autorDeTicket
        holder.lbemailDeAutor.text = ticket.emailDeAutor
        holder.lbfechaDeCreacionDeTicket.text = ticket.fechaDeCreacionDeTicket
        holder.lbestadoDeTicket.text = ticket.estadoDeTicket
        holder.lbfechaDeFinalizacionDeTicket.text = ticket.fechaDeFinalizacionDeTicket

        holder.imgEliminar.setOnClickListener{

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar la mascota?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(ticket.tituloDeTicket, position)
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
            txt1.setText(ticket.tituloDeTicket)
            val txt2 = EditText(context)
            layout.addView(txt2)
            txt2.setText(ticket.descripcionDeTicket)
            val txt3 = EditText(context)
            layout.addView(txt3)
            txt3.setText(ticket.autorDeTicket)
            val txt4 = EditText(context)
            layout.addView(txt4)
            txt4.setText(ticket.emailDeAutor)
            val txt5 = EditText(context)
            txt5.setText(ticket.estadoDeTicket)
            layout.addView(txt5)
            val txt6 = EditText(context)
            txt6.setText(ticket.fechaDeFinalizacionDeTicket)
            layout.addView(txt6)

            val uuid = ticket.uuid

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
