  create table Usuarios(
    UUID VARCHAR2(50) PRIMARY KEY,
    Correo NVARCHAR2 (50) ,
    NameUser NVARCHAR2(100),
    Password NVARCHAR2(10)
    );




create table Tickets (
UUID_Tickets NVARCHAR2 (40) PRIMARY KEY,
Titulo NVARCHAR2 (50),
Descripcion NVARCHAR2(200),
Autor NVARCHAR2 (100),
AutorEmail NVARCHAR2 (100),
CreationDate NVARCHAR2(20),
TicketStatus NVARCHAR2 (100),
FinishDate NVARCHAR2(20)

)
