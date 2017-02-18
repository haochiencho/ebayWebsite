package edu.ucla.cs.cs144;
import edu.ucla.cs.cs144.Item;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
 
    private IndexWriter indexWriter = null;

    public IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/"));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            indexWriter = new IndexWriter(indexDir, config);
            if(create){
				indexWriter.deleteAll(); // deletes previous index
				indexWriter.commit();
			}
        }
        return indexWriter;
    }

    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    public void indexItem(Item item) throws IOException {
        IndexWriter writer = getIndexWriter(false);
        Document doc = new Document();
        doc.add(new StringField("itemID", item.getItemID(), Field.Store.YES));
        doc.add(new TextField("name", item.getName(), Field.Store.YES));
        doc.add(new TextField("category", item.getCategory(), Field.Store.NO));
        doc.add(new TextField("description", item.getDescription(), Field.Store.NO));
        String fullSearchableText = item.getName() + " " + item.getCategory() + " " + item.getDescription();
        doc.add(new TextField("content", fullSearchableText, Field.Store.NO));
        writer.addDocument(doc);
    }


    public void rebuildIndexes() {

    	Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
	try {
	    conn = DbManager.getConnection(true);
	} catch (SQLException ex) {
	    System.out.println(ex);
	}


	/*
	 * Add your code here to retrieve Items using the connection
	 * and add corresponding entries to your Lucene inverted indexes.
         *
         * You will have to use JDBC API to retrieve MySQL data from Java.
         * Read our tutorial on JDBC if you do not know how to use JDBC.
         *
         * You will also have to use Lucene IndexWriter and Document
         * classes to create an index and populate it with Items data.
         * Read our tutorial on Lucene as well if you don't know how.
         *
         * As part of this development, you may want to add 
         * new methods and create additional Java classes. 
         * If you create new classes, make sure that
         * the classes become part of "edu.ucla.cs.cs144" package
         * and place your class source files at src/edu/ucla/cs/cs144/.
	 * 
	 */
	try {
        getIndexWriter(true);
    } catch (IOException exception) {
        exception.printStackTrace();
        System.exit(-1);
    }

	// Item[] items =
	Integer itemID; 
	String name, category, description;
    try {    
    	Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT * FROM item JOIN categoryList on item.itemID=categoryList.itemID");
	    
	    while (rs.next()) {
            itemID = rs.getInt("itemID"); //TODO: Maybe have a handler if for some bizarre reason itemID is null


            name = rs.getString("name");
            if (name == null)
                name = "";

            category = rs.getString("categoryList");
            if (category == null)
                category = "";

            description = rs.getString("description");
            if (description == null)
                description = "";

            Item item = new Item(Integer.toString(itemID), name, category, description);
            indexItem(item);
	    }

        stmt.close();
        rs.close();
	}
	catch (SQLException ex) {
	    System.err.println("SQLException: " + ex.getMessage());
	}
    catch (IOException exception) {
        exception.printStackTrace();
        System.exit(-1);      
    }

    try {
	    closeIndexWriter();
    } catch (IOException exception) {
        exception.printStackTrace();
        System.exit(-1);      
    }

        // close the database connection
	try {
	    conn.close();
	} catch (SQLException ex) {
	    System.out.println(ex);
	}
    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
