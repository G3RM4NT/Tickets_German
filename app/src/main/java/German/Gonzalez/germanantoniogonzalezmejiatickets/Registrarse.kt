package German.Gonzalez.germanantoniogonzalezmejiatickets

import Modelo.ClaseConexion
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Registrarse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrarse)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtnombreR = findViewById<EditText>(R.id.txtNombreR)
        val txtCorreoR = findViewById<EditText>(R.id.txtCorreoR)
        val txtcontrasenaR = findViewById<EditText>(R.id.txtContrasenaR)
        val btnRegistrarmeR = findViewById<Button>(R.id.btnRegistrarmeR)
        val btnregresarLogin = findViewById<Button>(R.id.btnregresarLogin)

        btnRegistrarmeR.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                //Creo un objeto de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //Creo una variable que contenga un PrepareStatement
                val crearUsuario =
                    objConexion?.prepareStatement("INSERT INTO Usuarios(UUID, Correo,NameUser, Password) VALUES (?, ?, ?, ?)")!!
                crearUsuario.setString(1, UUID.randomUUID().toString())

                crearUsuario.setString(2, txtCorreoR.text.toString())

                crearUsuario.setString(3, txtnombreR.text.toString())

                crearUsuario.setString(4, txtcontrasenaR.text.toString())
                crearUsuario.executeUpdate()
                withContext(Dispatchers.Main) {
                    //Abro otra corrutina o "Hilo" para mostrar un mensaje y limpiar los campos
                    //Lo hago en el Hilo Main por que el hilo IO no permite mostrar nada en pantalla
                    Toast.makeText(this@Registrarse, "Usuario creado", Toast.LENGTH_SHORT).show()
                    txtCorreoR.setText("")
                    txtnombreR.setText("")
                    txtcontrasenaR.setText("")
                }


            }

        }
        btnregresarLogin.setOnClickListener {
            val pantallaLogin = Intent(this, Login::class.java)
            startActivity(pantallaLogin)
    }
}
}



