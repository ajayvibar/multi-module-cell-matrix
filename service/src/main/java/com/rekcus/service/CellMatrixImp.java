package com.rekcus.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import org.apache.commons.io.output.FileWriterWithEncoding;
import com.rekcus.model.Cell;
import com.rekcus.util.InputUtil;
/**
*   Exercise 4
*   - Modified Exercise 2 as maven project
*   - Modified some code to use apche commons instead
*
*   java version: 1.8.0
*   author: Aron Vibar
*   date: 03/07/2019
*/

public class CellMatrixImp implements CellMatrix{   
    private static int CELL_LENGTH = 3;
    private static char FIELD_SEPARATOR = (char)31;
    private static char CELL_SEPARATOR = (char)30;

    private List<List<Cell>> matrix;    

    public CellMatrixImp() {
        this.matrix = new ArrayList<List<Cell>>();  
    }

    private Cell generateCell() {
        Cell cell;
        String str1; 
        String str2;

        do{
            str1 = InputUtil.generateString(CELL_LENGTH);
        }while(!(this.isUniqueCell(str1)));

        str2 = InputUtil.generateString(CELL_LENGTH);
        cell = new Cell(str1,str2);
        return cell;
    }

    public void generateMatrix(File file) throws IOException {
        List<Cell> cellRow;
        int rows = InputUtil.getInteger("Enter number of rows: ", false);
        int cols = InputUtil.getInteger("Enter number of columns: ", false);
        this.matrix = new ArrayList<List<Cell>>();

        for (int i=0;i<rows;i++) {
            cellRow = new ArrayList<Cell>();
            for (int j=0;j<cols;j++) {
                cellRow.add(generateCell());
            }
            this.matrix.add(cellRow);
        }

        System.out.println("Matrix generated.");
        saveMatrix(file);
    }

    @Override
    public void search() {
        String substring = InputUtil.getStringInput("Search String: ");
        int count = 0;
        boolean found = false;

        for (List<Cell> row : this.matrix) {
            for (Cell cell : row) {
                count = InputUtil.findSubstring(substring,cell.getKey());
                InputUtil.printSearchResult(substring,this.matrix.indexOf(row)
                                               ,this.matrix.get(this.matrix.indexOf(row)).indexOf(cell)
                                               ,0,count);
                if(count > 0) {
                    found = true;
                }

                count = InputUtil.findSubstring(substring,cell.getValue());
                InputUtil.printSearchResult(substring,this.matrix.indexOf(row)
                                               ,this.matrix.get(this.matrix.indexOf(row)).indexOf(cell)
                                               ,1,count);
                if(count > 0) {
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println(substring + " was not found.");
        }

    }

    @Override
    public void edit(File file) throws IOException{
        int index;
        int row = InputUtil.getInteger("Enter row: ",this.matrix.size(), true);
        int col = InputUtil.getInteger("Enter column: ",this.matrix.get(row).size(),true);
        
        do{
            index = InputUtil.getInteger("Enter either 0 or 1 or 2: ", true);
            
            if(index == 0 || index == 1 || index == 2){
                if(index == 0){
                    this.matrix.get(row).get(col).setKey(
                       this.getUniqueString("Replacement Key: "));
                }else if (index == 1){
                    this.matrix.get(row).get(col).setValue(
                        InputUtil.getStringInput("Replacement Value: "));
                }else{
                    this.matrix.get(row).get(col).setKey(
                        this.getUniqueString("Replacement Key: "));
                    this.matrix.get(row).get(col).setValue(
                        InputUtil.getStringInput("Replacement Value: "));
                }
                break;
            }
        }while(true);

        saveMatrix(file);
    }

    @Override
    public void print() {
        System.out.println();
        for (List<Cell> row : this.matrix) {
            for (Cell t : row){
                System.out.print(t.toString());
                System.out.print("|");
            }
            
            System.out.println();
        }
    }

    @Override
    public void reset(File file) throws IOException{
        generateMatrix(file);
        saveMatrix(file);
    }

    @Override
    public void addRow(File file) throws IOException{
        List<Cell> cellRow;
        cellRow = new ArrayList<Cell>();
        cellRow.add(generateCell());
        this.matrix.add(cellRow);
        System.out.println("New row added.");
        saveMatrix(file);
    }

    @Override
    public void addCell(File file) throws IOException{
        Cell cell;
        int row = InputUtil.getInteger("Enter row: ", this.matrix.size() ,true);
        String str = this.getUniqueString("Enter key: ");
        String str2 = InputUtil.getStringInput("Enter Value: ");

        this.matrix.get(row).add(new Cell(str,str2));

        System.out.println("Cell added to row " + row);
        saveMatrix(file);
    }

    @Override
    public void sort(File file) throws IOException{
        for (List<Cell> row : this.matrix) {
            Collections.sort(row, new Comparator<Cell>() {
                public int compare(Cell a, Cell b){
                    return a.getCellString().compareTo(b.getCellString());
                }
            });
        }
        
        System.out.println("Matrix sorted.");
        saveMatrix(file);
    }


    public void saveMatrix(File file) throws IOException {                                      
        FileWriterWithEncoding fw = new FileWriterWithEncoding(file,StandardCharsets.UTF_8);
        BufferedWriter out = new BufferedWriter(fw);
        
        for (List<Cell> row : this.matrix) {
            for (Cell cell : row){
                String str = cell.getKey() + FIELD_SEPARATOR + cell.getValue() 
                                 + CELL_SEPARATOR;
                out.write(str);
                out.flush();
            }
            out.write("\n");
            out.flush();
       }
       out.close();
    }

    public void loadMatrix(File file) throws IOException {
        String line;
        String[] row;
        String[] cellEntry;
        Cell cell;
        List<Cell> cellRow;
        Map<String,String> keys = new HashMap<>();
            
        try{
           InputStreamReader fr = new InputStreamReader(new FileInputStream(file),
                                                        StandardCharsets.UTF_8);
           BufferedReader br = new BufferedReader(fr);
                
           while((line = br.readLine())!=null) {
               row = line.split(String.valueOf(CELL_SEPARATOR));
               cellRow = new ArrayList<Cell>();
               for(String temp : row){     
                    cellEntry = temp.split(String.valueOf(FIELD_SEPARATOR));
                      
                    if(keys.containsKey(cellEntry[0])) {
                        System.out.println("Error: " + cellEntry[0] + " already exists!");
                        System.exit(0);
                    }

                    keys.put(cellEntry[0],cellEntry[1]);

                    cell = new Cell(cellEntry[0], cellEntry[1]);
                    cellRow.add(cell);
               }
               this.matrix.add(cellRow);
            }
            
            fr.close();
                
        }catch(Exception e){}
        
        System.out.println("Matrix loaded.");
    }

    private boolean isUniqueCell(String entry) {
        for (List<Cell> row : this.matrix) {
            for(Cell cell : row) {
                if(cell.getKey().equals(entry)) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getUniqueString(String message) {
        String str;
        boolean unique = false;
        
        do {
            str = InputUtil.getStringInput("Enter key: ");
            unique = this.isUniqueCell(str);

            if(!unique) {
                System.out.println("Key " + str + " already exists.");
            }

        }while(!unique);

        return str;
    }
}