/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package memoriavirtual;

/**
 *
 * @author Luan Medeiros
 */
public class ByteProcesso {
    private String Processo;
    private int parte;
    private String valorByte = "_";

    public ByteProcesso(String Processo, int parte) {
        this.Processo = Processo;
        this.parte = parte;        
    }

    public int getParte() {
        return parte;
    }

    public void setParte(int parte) {
        this.parte = parte;
    }

    public String getProcesso() {
        return Processo;
    }

    public void setProcesso(String Processo) {
        this.Processo = Processo;
    }

    public String getValorByte() {
        return valorByte;
    }

    public void setValorByte(String valorByte) {
        this.valorByte = valorByte;
    }

    @Override
    public String toString() {
        return valorByte;
    }
    
    
      
}
