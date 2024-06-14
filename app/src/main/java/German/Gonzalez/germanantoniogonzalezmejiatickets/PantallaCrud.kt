package German.Gonzalez.germanantoniogonzalezmejiatickets

import German.Gonzalez.germanantoniogonzalezmejiatickets.R.id.txtTitulo
import Modelo.ClaseConexion
import Modelo.Ticket
import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PantallaCrud : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txttituloDeTicket = findViewById<TextView>(R.id.txtTitulo)
        val txtdescripcionDeTicket = findViewById<TextView>(R.id.txtDescripciónTicket)
        val txtautorDeTicket = findViewById<TextView>(R.id.txtAutor)
        val txtemailDeAutor = findViewById<TextView>(R.id.txtEmailautor)
        val txtfechaDeCreacionDeTicket = findViewById<TextView>(R.id.txtFechacreacion)
        val txtestadoDeTicket = findViewById<TextView>(R.id.txtestadoTicket)
        val txtfechaDeFinalizacionDeTicket = findViewById<TextView>(R.id.txtFechafinalticket)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val rcvTickets = findViewById<RecyclerView>(R.id.rcvTicket)

        rcvTickets.layoutManager = LinearLayoutManager(this)

        fun obtenerTickets(): List<Ticket> {
            val objCon = ClaseConexion().cadenaConexion()
            val statement = objCon?.createStatement()
            val resulSet = statement?.executeQuery("SELECT * FROM Tickets")!!

            val listTickets = mutableListOf<Ticket>()

            while (resulSet.next()) {
                val UUID = resulSet.getString("UUID_Tickets")
                val Titulo = resulSet.getString("Titulo")
                val Descripcion = resulSet.getString("Descripcion")
                val Autor = resulSet.getString("Autor")
                val Email = resulSet.getString("AutorEmail")
                val Creacion = resulSet.getString("CreationDate")
                val Estado = resulSet.getString("TicketStatus")
                val Finalizacion = resulSet.getString("Finishdate")

                val values =
                    Ticket(UUID, Titulo, Descripcion, Autor, Email, Creacion, Estado, Finalizacion)
                listTickets.add(values)
            }

            return listTickets
        }


        CoroutineScope(Dispatchers.IO).launch {
            val tickets = obtenerTickets()
            withContext(Dispatchers.Main)
            {
                val adp = Adaptador(tickets)
                rcvTickets.adapter = adp
            }
        }

        //Programar el boton de guardar
        btnGuardar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                //Crear objeto de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //Crear una variable que contenga un PrepareStatement
                val addTicket =
                    objConexion?.prepareStatement("insert into Tickets (UUID_Tickets,Titulo,Descripcion,Autor,AutorEmail,CreationDate,TicketStatus,FinishDate)VALUES (?, ?, ?, ?, ?, ?, ?, ?)")!!
                addTicket.setString(1, UUID.randomUUID().toString())
                addTicket.setString(2, txttituloDeTicket.text.toString())
                addTicket.setString(3, txtdescripcionDeTicket.text.toString())
                addTicket.setString(4, txtautorDeTicket.text.toString())
                addTicket.setString(5, txtemailDeAutor.text.toString())
                addTicket.setString(6, txtfechaDeCreacionDeTicket.text.toString())
                addTicket.setString(7, txtestadoDeTicket.text.toString())
                addTicket.setString(8, txtfechaDeFinalizacionDeTicket.text.toString())
                addTicket.executeUpdate()

            }
        }
    }
}