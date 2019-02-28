
package pexemploav2;
import java.io.Serializable;
import javax.persistence.*;

@Entity
public class Horasextra implements Serializable {
   private static final long serialVersionUID = 1L; 
   
    @Id 
    private String che;
    private int nhe ;

    public Horasextra() {
    }

    public Horasextra(String che, int nhe) {
        this.che = che;
        this.nhe = nhe;
    }

    public String getChe() {
        return che;
    }

    public void setChe(String che) {
        this.che = che;
    }

    public int getNhe() {
        return nhe;
    }

    public void setNhe(int nhe) {
        this.nhe = nhe;
    }

    @Override
    public String toString() {
        return "Horasextra{" + "che=" + che + ", nhe=" + nhe + '}';
    }
}