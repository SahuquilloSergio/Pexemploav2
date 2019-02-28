package pexemploav2;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class MetodosAv2 {
    
    //Variables que se necesitan para conectarse a la base de datos
    String driver = "jdbc:oracle:thin:";
    String host = "localhost.localdomain"; // tambien puede ser una ip como "192.168.1.14"
    String porto = "1521";
    String sid = "orcl";
    String usuario = "hr";
    String password = "hr";
    String url = driver + usuario + "/" + password + "@" + host + ":" + porto + ":" + sid;
    
    //para conectar co native protocal all java driver: 
    //creamos un obxecto Connection usando o metodo getConnection da clase  DriverManager            

    Connection conn = null;
    Statement stm = null;

    //variables que usaremos
    public int cinf;
    public String dniinf;
    public double nhe;
    public String che;
    public double salariototal;
    
    //Variables para conectarnos a mongo
    Mongo mongo = new Mongo("localhost", 27017);
    DB db = mongo.getDB("test");
    DBCollection collection = db.getCollection("empretodos");
    
    //Varibales para ObjectDB
    EntityManagerFactory baseDatos = Persistence.createEntityManagerFactory("$objectdb/db/horasextratodos.odb");
    EntityManager personas = baseDatos.createEntityManager();

    //Metodo para conectarnos a la base de datos
    public void conexion() {
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                System.out.println("Conectado");
            } else {
                System.out.println("Error al conectar");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            if (stm != null) {
                stm.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Metodo que realiza la ejecucion requerida en el enunciado
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws XMLDBException 
     */
    public void Ejecucion() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, XMLDBException {
        
        //Nos conectamos a SQL para sacar las variables cinf, dniinf y los hijos
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT cinf,dniinf, fillos from informaticos");
        while (rs.next()) {
            cinf = rs.getInt("cinf");
            dniinf = rs.getString("dniinf");
            
            //Crea una estructura que corresponde al objeto
            java.sql.Struct sfillos = (java.sql.Struct) rs.getObject("fillos");
            Object[] fillo = sfillos.getAttributes();
            //Debemos parsear
            java.math.BigDecimal nfhome = (java.math.BigDecimal) fillo[0];
            java.math.BigDecimal nfmuller = (java.math.BigDecimal) fillo[1];
            int filloshomes = nfhome.intValue();
            int fillasmulleres = nfmuller.intValue();
            int fillostotal;
            fillostotal = filloshomes + fillasmulleres;
            //Mostramos por pantalla la cantidad de hijos
            if ((fillostotal > 0)) {
                System.out.println("\ncinf: " + cinf
                        + " dniinf: " + dniinf
                        + "\nfillos homes: " + filloshomes
                        + " fillas mulleres: " + fillasmulleres
                        + "\n");

               
                //VAMOS A MONGO A POR LAS VARIABLES SB, PHE y CHE
                BasicDBObject whereQuery = new BasicDBObject();
                //Condicion que le mandamos
                whereQuery.put("dnie", dniinf);
                //Cogemos las variables por separado
                double sb = (double) collection.find(whereQuery).one().get("sb");
                double phe = (double) collection.find(whereQuery).one().get("phe");
                String chem = (String) collection.find(whereQuery).one().get("che");
                //Imprimimos las variables
                System.out.println("sb: " + sb + "\nphe: " + phe + "\nchem: " + chem + "\n");
                
                //VAMOS A LA BASE HORASEXTRA Y TRABAJAMOS CON OBJECTDB
                //Sentencia sql(k es una referencia)
                TypedQuery<Horasextra> query = personas.createQuery("SELECT k FROM Horasextra k", Horasextra.class);
                //Recogemos todos los objetos
                List<Horasextra> results = query.getResultList();
                //Recorremos los objetos y sacamos las variables por separado
                for (Horasextra k : results) {
                    double nhe = k.getNhe();
                    che = k.getChe();
                    if (chem.equalsIgnoreCase(che)) {
                        //Mostramos por pantalla
                        System.out.println("numero he: " + nhe);
                        //Hacemos la suma final y lo mostramos
                        salariototal = (sb + phe * nhe) + (fillostotal * 100);
                        System.out.println("Salario Total: " + salariototal+"\n*****************************************");
                    }
                }
                
                //NOS VAMOS A EXIST A ESCRIBIR EN EL XML FINAL
                //Declaramos las variables necesarias
                String driver = "org.exist.xmldb.DatabaseImpl";
                Class<?> cl = Class.forName(driver);
                Database database = (Database) cl.newInstance();
                DatabaseManager.registerDatabase(database);
                String coleccion = "/db";
                String recursos = "/db/cousas";
                Collection col;
                Collection col2;
                String uri = "xmldb:exist://localhost:8080/exist/xmlrpc";
                col = DatabaseManager.getCollection(uri + coleccion, "admin", "");
                col2 = DatabaseManager.getCollection(uri + recursos, "admin", "");
                
                //Ruta a la coleccion, escribimos en el fichero
                XPathQueryService servicio = (XPathQueryService) col2.getService("XPathQueryService", "1.0");
                String cons = "update insert <Informatico><cinf>" + cinf + "</cinf><salariototal>"+salariototal+"</salariototal></Informatico>  into /informaticos";
                ResourceSet result = servicio.query(cons);

            }

        }
    }


}
