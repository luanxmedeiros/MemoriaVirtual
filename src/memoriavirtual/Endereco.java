/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package memoriavirtual;

import java.util.ArrayList;

/**
 *
 * @author Luan Medeiros
 */
public class Endereco {
    private String processoDono = "";
    private String endereco;
    private int indiceDecimal;
    private ArrayList<ByteProcesso> bytes = new ArrayList<>();    

    public Endereco(String endereco, int indiceDecimal) {        
        this.endereco = endereco;
        this.indiceDecimal = indiceDecimal;
        
    }
    
    
    
    public void insereByte (ByteProcesso byteproc)
    {
        bytes.add(byteproc);
    }

    public String getProcessoDono() {
        return processoDono;
    }

    public void setProcessoDono(String processoDono) {
        this.processoDono = processoDono;
    }

    public String getEndreco() {
        return endereco;
    }

    public void setEndereco(String endreco) {
        this.endereco = endreco;
    }

    public int getIndiceDecimal() {
        return indiceDecimal;
    }

    public void setIndiceDecimal(int indiceDecimal) {
        this.indiceDecimal = indiceDecimal;
    }

    public ArrayList<ByteProcesso> getBytes() {
        return bytes;
    }

    public void setBytes(ArrayList<ByteProcesso> bytes) {
        this.bytes = bytes;
    }
    
    public void limparBytes()
    {
        bytes.clear();
    }

    @Override
    public String toString() {
        String bytesString = "";
        //for(ByteProcesso bytesStr : bytes) bytesString += bytesStr.getValorByte()+" - ";
    for(ByteProcesso bytesStr : bytes) bytesString += " [ "+bytesStr+" ] ";
        
       // bytesString = bytesString.substring(0, bytesString.length() - 2);
        return bytesString;
    }
    
    
    
    
}
