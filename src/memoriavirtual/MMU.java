package memoriavirtual;    
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


//
/**
 *
 * @author Luan Medeiros
 */
public class MMU extends javax.swing.JFrame {
    private ArrayList<Endereco> memoriaArray = new ArrayList<>();
    private ArrayList<Endereco> hdArray = new ArrayList<>();
    private ArrayList<Processo> processos = new ArrayList<>();
    
   
    private int memoriaMaxima = 50;
    private int hdMaximo = 500; 
    private int tamanhoMMU = 500;
    private final static Charset ENCODING = StandardCharsets.UTF_8; 
   
    int tamanhoVetorProcessos = 0;
    
    public int indexUltimoAlocadoHD()
    {
        for(Endereco end : hdArray)
        {
            if(end.getProcessoDono().equals("")) return hdArray.indexOf(end);
        }
        
        return hdArray.size()-1;
    }

    public Endereco escolhePageParaRemover(String processo)
    {                             
            int pageSorteada = (int)(Math.random() * 9);
            return memoriaArray.get(pageSorteada);                            
    }
    public void atualizaTabelaHD()
    {
        for(int x = 0; x < hdArray.size() ; x++ )
        {
            TabelaHD.setValueAt("", x, 1);
            TabelaHD.setValueAt("", x, 2);
        }
        for (int i = 0; i < hdArray.size(); i++) {            
            String bytesStr = hdArray.get(i).toString();
            TabelaHD.setValueAt(bytesStr, i, 1);
            TabelaHD.setValueAt(hdArray.get(i).getProcessoDono(), i, 2);
        }         
    }
    
        public void atualizaTabelaMemoria()
    {
        for(int x = 0; x <memoriaArray.size() ; x++ ) 
        {
            TabelaMemoria.setValueAt("", x, 1);
            TabelaMemoria.setValueAt("", x, 2);
        }
        for (int i = 0; i < memoriaArray.size(); i++) {            
            String bytesStr = memoriaArray.get(i).toString();
            TabelaMemoria.setValueAt(bytesStr, i, 1);
            TabelaMemoria.setValueAt(memoriaArray.get(i).getProcessoDono(), i, 2);
        }        
    }
    
    
        public Endereco removePageDoHD(String processo, int parte)
    {
        Endereco remover = null;
        for(Endereco end : hdArray)
        {
            if(end.getProcessoDono().equals(processo))
            {
                for(ByteProcesso byteProc : end.getBytes())
                if(byteProc.getParte()==parte)
                {
                    remover = end;                    
                    break;                    
                }
            }
        }        
        return remover;       
    }
   
    public boolean naMemoria (int parteProcesso, String processo)
    {
        for(Endereco end : memoriaArray)
        {   if(end.getProcessoDono().equalsIgnoreCase(processo))
            {
                for(ByteProcesso byteProc : end.getBytes())
                {
                    if(parteProcesso == byteProc.getParte())
                    {
                        return true;                        
                    }
                }   
            }
        }        
        return false;
    }
    
    public void gravar(int parte, String processo, String conteudo)
    {   boolean gravado = false;
        for(Endereco end : memoriaArray)
        {   
            if(end.getProcessoDono().equals(processo))
            {
                for(ByteProcesso byteProc : end.getBytes())
                {
                    if(parte == byteProc.getParte())
                    {
                        byteProc.setValorByte(conteudo);
                        gravado = true;
                        break;
                    }
                }   
            }
            if(gravado == true) break;
        }        
    }
    

    /* Inserindo no memoriaArray todos os objetos processos com tamanho a partir do arquivo TXT, até que o 
    tamanho da soma dos processos exceda o tamanho da memória */
    public void preencheMemoria(List<String> texto)
    {        
        Iterator itr = texto.iterator();       
        int cont = 0;
        while(itr.hasNext())
        {         
            int memoriaConsumidaProcesso = Integer.parseInt(itr.next().toString());                                    
            String nomeProcesso = Character.toString((char)(65+cont));                                    
            processos.add(new Processo(nomeProcesso, memoriaConsumidaProcesso));                            
            cont++;
        }
        

            int contPage = 0;
            int contPageHD = 0;
            for(Processo proc  : processos)
            {
                if(contPage > 9)
                {
                    if(proc.getTamanho() <= 5)
                    {
                        for(int x = 0; x < proc.getTamanho(); x++)
                        {
                            hdArray.get(contPageHD).insereByte(new ByteProcesso(proc.getNome(), x));
                            hdArray.get(contPageHD).setProcessoDono(proc.getNome());
                        }
                        contPageHD++;
                    }
                    else
                    {   
                        for(int x = 0; x < 5; x++)
                        {
                            hdArray.get(contPageHD).insereByte(new ByteProcesso(proc.getNome(), x));
                            hdArray.get(contPageHD).setProcessoDono(proc.getNome());
                        }
                        contPageHD++;
                        //Envia o restante para o HD
                        int contador = proc.getTamanho()-5;
                        int posCont = 5;                                                
                        while(true)
                        {                        
                            int inc = 0;
                            for(int j = 0; j < 5; j++)
                            {       
                                    if(contador == 0) break;
                                    hdArray.get(contPageHD).insereByte(new ByteProcesso(proc.getNome(), posCont));
                                    hdArray.get(contPageHD).setProcessoDono(proc.getNome());     
                                    contador--;
                                    posCont++;
                            }
                            contPageHD++;
                            if(contador == 0) break;                            
                        }

                    }
                    
                }
                else if(proc.getTamanho() <= 5)
                {
                    for(int x = 0; x < proc.getTamanho(); x++)
                    {
                        memoriaArray.get(contPage).insereByte(new ByteProcesso(proc.getNome(), x));
                        memoriaArray.get(contPage).setProcessoDono(proc.getNome());
                    }
                    
                }
                else
                {   
                    
                    for(int x = 0; x < 5; x++)
                    {
                        memoriaArray.get(contPage).insereByte(new ByteProcesso(proc.getNome(), x));
                        memoriaArray.get(contPage).setProcessoDono(proc.getNome());                        
                    }
                        //Envia o restante para o HD                                   
                        int contador = proc.getTamanho()-5;
                        int posCont = 5;                        
                        while(true)
                        {                        
                            int inc = 0;
                            for(int j = 0; j < 5; j++)
                            {       
                                    if(contador == 0) break;
                                    hdArray.get(contPageHD).insereByte(new ByteProcesso(proc.getNome(), posCont));
                                    hdArray.get(contPageHD).setProcessoDono(proc.getNome());     
                                    contador--;
                                    posCont++;
                            }
                            contPageHD++;
                            if(contador == 0) break;                            
                        }
                                       
                }
                contPage++; 
            }                        
    }
    
   
    //Preenchendo o select com os nomes dos processos.
    public void preencheSelect()
    {
        //Criando vetor com os nomes dos processos para inserir no select
        String[] processosVetor = new String[processos.size()+1];
        int cont2 = 0;
        processosVetor[0] = "";
        ArrayList<String> teste = new ArrayList<>();
        for(int x = 1; x < processosVetor.length; x++)
        {
            processosVetor[x] = "Processo "+Character.toString((char)(65+cont2));
            cont2++;
        }
        //Aplicando valores ao select         
        processosSelect.setModel(new javax.swing.DefaultComboBoxModel(processosVetor)); 
    }
    
        public void preencheSelectPosicao(int tam)
    {
        //Criando vetor com os nomes dos processos para inserir no select
        String[] processosVetor = new String[tam];        
        for(int x = 0; x < tam; x++)
        {
            processosVetor[x] = ""+x;            
        }
        //Aplicando valores ao select         
        posicaoSelect.setModel(new javax.swing.DefaultComboBoxModel(processosVetor));
        posicaoSelect.setSelectedIndex(0);        
    }
    
    public void configuraTabela(int quanTlinhas, JTable tabela)
    {
        ////////////////////   Configurando a tabela  ///////////////////////
        DefaultTableModel modeloTabela = (DefaultTableModel) tabela.getModel();  //Pega o modelo padrão da tabela
        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer(); // Criando uma célula de redenrizar para poder centralizar
        centralizado.setHorizontalAlignment(SwingConstants.CENTER); //Setando o alinhamento horizontal centralizado
        
        //Aplicando o alinhamento centralizado nas duas colunas
        tabela.getColumnModel().getColumn(0).setCellRenderer(centralizado); 
        tabela.getColumnModel().getColumn(1).setCellRenderer(centralizado);
        tabela.getColumnModel().getColumn(2).setCellRenderer(centralizado);
        
        //Adicionando linhas com os espações de memória no modelo da tabela
        
        int cont = 0;                
        if(tabela.equals(TabelaMemoria))
        {
            int posDecimal = 0;
            for(int i = 0; i < quanTlinhas; i +=5)
            {  
                    int ini = i;
                    int fim = i+4;
                    String posicaoString = ini+"B - "+fim+"B";
                    modeloTabela.addRow(new String[]{posicaoString,""});
                    memoriaArray.add(new Endereco(posicaoString, posDecimal));                
                    posDecimal ++;                
            }            
        }
        else if(tabela.equals(TabelaHD))
        {   
            int posDecimal = 0;
            for(int i = 0; i < quanTlinhas; i+=5)
            {
                    int ini = i;
                    int fim = i+4;
                    String posicaoString = ini+"B - "+fim+"B";
                    modeloTabela.addRow(new String[]{posicaoString,""});
                    hdArray.add(new Endereco(posicaoString, posDecimal));
            }              
        }
    }
    
    
    /** Creates new form GerenciadorDeMemoria */
    public MMU() 
    {
        initComponents();
      
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        registroLimite = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelaMemoria = new javax.swing.JTable();
        carregar = new javax.swing.JButton();
        areaProcesso = new javax.swing.JPanel();
        gravarBotao = new javax.swing.JButton();
        valorTexto = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        processosSelect = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        posicaoSelect = new javax.swing.JComboBox<>();
        aviso = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        TabelaScroolHD = new javax.swing.JScrollPane();
        TabelaHD = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gerenciador de Memória");
        setResizable(false);

        registroLimite.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        TabelaMemoria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Page", "Conteúdo", "Processo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TabelaMemoria.setToolTipText(""); // NOI18N
        TabelaMemoria.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(TabelaMemoria);
        TabelaMemoria.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (TabelaMemoria.getColumnModel().getColumnCount() > 0) {
            TabelaMemoria.getColumnModel().getColumn(0).setResizable(false);
            TabelaMemoria.getColumnModel().getColumn(2).setResizable(false);
        }

        carregar.setText("CARREGAR");
        carregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                carregarActionPerformed(evt);
            }
        });

        areaProcesso.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        gravarBotao.setText("GRAVAR");
        gravarBotao.setEnabled(false);
        gravarBotao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gravarBotaoActionPerformed(evt);
            }
        });

        valorTexto.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel2.setText("Valor");

        processosSelect.setAutoscrolls(true);
        processosSelect.setEnabled(false);
        processosSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processosSelectActionPerformed(evt);
            }
        });

        jLabel5.setText("Posição Processo");

        jLabel9.setText("Processo");

        posicaoSelect.setEnabled(false);
        posicaoSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                posicaoSelectActionPerformed(evt);
            }
        });

        aviso.setEditable(false);
        aviso.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        aviso.setForeground(new java.awt.Color(255, 0, 0));
        aviso.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout areaProcessoLayout = new javax.swing.GroupLayout(areaProcesso);
        areaProcesso.setLayout(areaProcessoLayout);
        areaProcessoLayout.setHorizontalGroup(
            areaProcessoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(areaProcessoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(areaProcessoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(areaProcessoLayout.createSequentialGroup()
                        .addGroup(areaProcessoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(areaProcessoLayout.createSequentialGroup()
                                .addGroup(areaProcessoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(areaProcessoLayout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(valorTexto, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(areaProcessoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                        .addComponent(processosSelect, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(34, 34, 34)
                                .addComponent(gravarBotao))
                            .addComponent(posicaoSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addComponent(aviso, javax.swing.GroupLayout.PREFERRED_SIZE, 818, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        areaProcessoLayout.setVerticalGroup(
            areaProcessoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(areaProcessoLayout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(areaProcessoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(areaProcessoLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(processosSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(posicaoSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addGroup(areaProcessoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(valorTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(gravarBotao))
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, areaProcessoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(aviso, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Memória");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setText("HD");

        TabelaHD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Endereço", "Conteúdo", "Processo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TabelaScroolHD.setViewportView(TabelaHD);
        if (TabelaHD.getColumnModel().getColumnCount() > 0) {
            TabelaHD.getColumnModel().getColumn(2).setResizable(false);
        }

        javax.swing.GroupLayout registroLimiteLayout = new javax.swing.GroupLayout(registroLimite);
        registroLimite.setLayout(registroLimiteLayout);
        registroLimiteLayout.setHorizontalGroup(
            registroLimiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registroLimiteLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(registroLimiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(registroLimiteLayout.createSequentialGroup()
                        .addGroup(registroLimiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(areaProcesso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(carregar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(91, 91, 91))
                    .addGroup(registroLimiteLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(registroLimiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 608, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                        .addGroup(registroLimiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TabelaScroolHD, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35))))
        );
        registroLimiteLayout.setVerticalGroup(
            registroLimiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registroLimiteLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(carregar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(areaProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(registroLimiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(registroLimiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addComponent(TabelaScroolHD, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(registroLimite, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(registroLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void processosSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processosSelectActionPerformed
        for(Processo proc : processos)
        if(proc.getNome().equals(processosSelect.getSelectedItem().toString().replace("Processo ", "")))
        {
            preencheSelectPosicao(proc.getTamanho());
            posicaoSelect.setEnabled(true);
            break;
        }
        gravarBotao.setEnabled(true);        
    }//GEN-LAST:event_processosSelectActionPerformed

    private void gravarBotaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gravarBotaoActionPerformed
        aviso.setText("");
        if(!processosSelect.getSelectedItem().toString().equals("") && 
           !posicaoSelect.getSelectedItem().toString().equals("") &&
           posicaoSelect.isEnabled())
        {
            Endereco endereco;
            String processo = processosSelect.getSelectedItem().toString().replace("Processo ", "");
            int parte = Integer.parseInt(posicaoSelect.getSelectedItem().toString());
            if(naMemoria(parte, processo)==true)
            {
                gravar(parte, processo, valorTexto.getText());
            }
            else
            {
                aviso.setText("PAGE FAULT");
                endereco = escolhePageParaRemover(processo);                
                int posDec = memoriaArray.indexOf(endereco);                
                hdArray.add(indexUltimoAlocadoHD(), endereco);
                memoriaArray.remove(endereco);
                Endereco removido = removePageDoHD(processo, parte);                
                memoriaArray.add(posDec, removido);                
                hdArray.remove(removido);
                gravar(parte, processo, valorTexto.getText());                                                                
            }
            atualizaTabelaHD();
            atualizaTabelaMemoria();

        }
        else aviso.setText("É NECESSÁRIO SELECIONAR O PROCESSO E A POSIÇÃO");

    }//GEN-LAST:event_gravarBotaoActionPerformed

    private void carregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_carregarActionPerformed
        memoriaArray.clear();
        processosSelect.setEnabled(true);
        gravarBotao.setEnabled(true);
        carregar.setEnabled(false);

        //Pegando caminho relativo
        String diretorioString = System.getProperty("user.dir");
        System.out.println("CAMINHO ABSOLUTO DO PROJETO "+diretorioString);
        diretorioString = diretorioString.replace("\\", "/");
            diretorioString += "/src/memoriavirtual/";
            Path path = Paths.get(diretorioString+"processos.txt");

            List <String> texto = new ArrayList<>();
            try {
                texto = Files.readAllLines(path, ENCODING);
            } catch (IOException ex) {
                Logger.getLogger(MMU.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        
            
        configuraTabela(memoriaMaxima, TabelaMemoria);              
        configuraTabela(hdMaximo, TabelaHD);                
        preencheMemoria(texto);
        atualizaTabelaHD();
        atualizaTabelaMemoria();
        preencheSelect();                
    }//GEN-LAST:event_carregarActionPerformed

    private void posicaoSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_posicaoSelectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_posicaoSelectActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MMU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MMU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MMU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MMU.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MMU().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TabelaHD;
    private javax.swing.JTable TabelaMemoria;
    private javax.swing.JScrollPane TabelaScroolHD;
    private javax.swing.JPanel areaProcesso;
    private javax.swing.JTextField aviso;
    private javax.swing.JButton carregar;
    private javax.swing.JButton gravarBotao;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> posicaoSelect;
    private javax.swing.JComboBox<String> processosSelect;
    private javax.swing.JPanel registroLimite;
    private javax.swing.JTextField valorTexto;
    // End of variables declaration//GEN-END:variables
}
