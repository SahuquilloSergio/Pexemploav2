package pexemploav2;

import java.sql.SQLException;
import org.xmldb.api.base.XMLDBException;

/*Se Pide:
para cada informatico da taboa oracle -informaticos- que cumpla que o 
numero de fillos totais  (homes+mulleres) e maior que 0 debe :
inserir dito   informatico  no ficheiro -final.xml- da colecion 
-cousas- da base Exists gardando dous tags que seran o seu -cinf- 
e o -salariototal- de dito empregado sabendo que o salario total se calcula asi:

salario total= sb+phe*nhe+ nh*100

sabendo que:
os valores -sb- e -phe- de cada informatico  atopanse
na base mongo -test- colecion -empretodos-  na fila que ten por -dnie- o
-dniinf- do informatico .

o valor -nhe- atopase na base objectdb -horasestratodos.odb- 
e corresponde a un obxecto da clase -Horasextra- que ten por valor do 
atributo -che- o valor correspondente ao valor -che- da mesma fila da colecion 
-empretodos-  mencionada no  parrafo anterior. */

public class Pexemploav2 {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, XMLDBException, IllegalAccessException {

        
        MetodosAv2 m = new MetodosAv2();
        
        m.conexion();
        m.Ejecucion();
        m.close();
    }
    
}
