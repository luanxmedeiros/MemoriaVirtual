
package memoriavirtual;



/**
 *
 * @author Luan Medeiros
 */
public class Processo implements Cloneable {
    private String nome;
    private int tamanho = 0;    
     
    
    public Processo(String nome, int tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
          
    }

   //Métodos get
    public String getNome() {
        return nome;
    }
    public int getTamanho() {
        return tamanho;
    }


  
    
}
