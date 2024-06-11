package RecyclerViewHelper

import German.Gonzalez.germanantoniogonzalezmejiatickets.R
import Modelo.ClaseConexion
import Modelo.Ticket
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Adaptador(private var Datos: List<Ticket>) : RecyclerView.Adapter<ViewHolder>() {
    fun actualizarLista(nuevaLista: List<Ticket>) {
        Datos = nuevaLista
        notifyDataSetChanged() // Notificar al adaptador sobre los cambios
    }

    fun actualicePantalla(Titulo: String, Descripcion: String, Autor: String, AutorEmail: String, CreationDate: String, TicketStatus: String, Finishdate: String,uuid : String){
        val index = Datos.indexOfFirst { it.UUID_Tickets == uuid }
        Datos[index].Titulo = Titulo
        Datos[index].Descripcion = Descripcion
        Datos[index].Autor = Autor
        Datos[index].AutorEmail = AutorEmail
        Datos[index].CreationDate = CreationDate
        Datos[index].TicketStatus = TicketStatus
        Datos[index].Finishdate = Finishdate



        notifyDataSetChanged()
    }  



    /////////////////// TODO: Eliminar datos
    fun eliminarDatos(nombreMascota: String, posicion: Int){
        //Actualizo la lista de datos y notifico al adaptador
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO){
            //1- Creamos un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Crear una variable que contenga un PrepareStatement
            val deleteMascota = objConexion?.prepareStatement("delete from tbMascotas where nombreMascota = ?")!!
            deleteMascota.setString(1, nombreMascota)
            deleteMascota.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        // Notificar al adaptador sobre los cambios
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    //////////////////////TODO: Editar datos
    fun actualizarDato(Titulo: String, Descripcion: String, Autor: String, AutorEmail: String, CreationDate: String, TicketStatus: String, Finishdate: String, uuid : String){
        GlobalScope.launch(Dispatchers.IO){

            //1- Creo un objeto de la clase de conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- creo una variable que contenga un PrepareStatement
            val UpdateTicket = objConexion?.prepareStatement("update  Tickets set Titulo = ? ,Descripcion = ?, Autor = ?, AutorEmail = ?, CreationDate = ?, TicketStatus = ?, FinishDate = ? where UUID_Tickets = ?")!!
            UpdateTicket.setString(1, Titulo )
            UpdateTicket.setString(2, Descripcion)
            UpdateTicket.setString(3, Autor)
            UpdateTicket.setString(4, AutorEmail)

            UpdateTicket.setString(5,CreationDate )

            UpdateTicket.setString(6, TicketStatus)

            UpdateTicket.setString(7, Finishdate)
            UpdateTicket.setString(8, uuid)



            withContext(Dispatchers.Main){
                actualicePantalla(Titulo,Descripcion,Autor,AutorEmail,CreationDate,TicketStatus,Finishdate, uuid)
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
        val mascota = Datos[position]
        holder.textView.text = mascota.nombreMascota

        //todo: clic al icono de eliminar
        holder.imgBorrar.setOnClickListener {

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar el ticket?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(mascota.nombreMascota, position)
            }

            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        //Todo: icono de editar
        holder.imgEditar.setOnClickListener{
            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar")
            builder.setMessage("¿Desea actualizar el ticket?")

            //Agregarle un cuadro de texto para
            //que el usuario escriba el nuevo nombre
            val cuadroTexto = EditText(context)
            cuadroTexto.setHint(mascota.nombreMascota)
            builder.setView(cuadroTexto)

            //Botones
            builder.setPositiveButton("Actualizar") { dialog, which ->
                actualizarDato(cuadroTexto.text.toString(), mascota.uuid)
            }

            builder.setNegativeButton("Cancelar"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        //Todo: Clic a la card completa
        //Vamos a ir a otra pantalla donde me mostrará todos los datos
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalle = Intent(context, detalle_mascota::class.java)
          //enviar a la otra pantalla todos mis valores
            pantallaDetalle.putExtra("MascotaUUID", mascota.uuid)
            pantallaDetalle.putExtra("nombre", mascota.nombreMascota)
            pantallaDetalle.putExtra("peso", mascota.peso)
            pantallaDetalle.putExtra("edad", mascota.edad)
            context.startActivity(pantallaDetalle)
        }




    }

}
