package batchFilter;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Santosh Dubey
 */
public class BatchJobsParser
{
    //~ Methods ----------------------------------------------------------------

    /**
     * <p>
     * This method will generate Linux (Shell) command which can be used in new
     * <b>'runStream.ksh'</b> script.
     * </p>
     *
     * @throws IOException
     *
     */
    public static void generateShellCommand(
        HashMap<String, ArrayList<String>> Batch )
      throws IOException
    {
        String batchStream = "";
        String filter = "";
        String fileExtension = "\".txt\"";
        String newFileExtension = "\"-new.txt\"";
        String newLine = "\'\\n\'";

        FileOutputStream f =
            new FileOutputStream(
                "..//BatchFilter//src//batchFilter//Console Log//Shell_Command_Log.txt" );

        ps.println( "\nGenerating 'Shell_Command_Log.txt' file..." );

        for ( Entry<String, ArrayList<String>> batchKey : Batch.entrySet( ) )
        {
            System.setOut( new PrintStream( f ) );

            if ( ( batchKey.getKey( ) != null ) && batchStream.isEmpty( ) )
            {
                batchStream = batchKey.getKey( );
                filter = batchKey.getValue( ).toString( );
                filter =
                    filter.replace( "[", "" ).replace( "]", "" ).replace( ",",
                        "" );

                System.out.println(
                    "-----------------------------------------------------" );
                System.out.println(
                    "Paste the following output in 'runStream.ksh' script:" );
                System.out.println(
                    "-----------------------------------------------------\n" );
                System.out.println( "\t\t\t\"" + batchKey.getKey( ) + "\")" +
                      "\n\t\t\techo \'" + filter +
                      "\' > $user_Directory/$stream" + fileExtension );
                System.out.println( "\t\t\t" + "tr ' ' " + newLine +
                      " < $user_Directory/$stream" + fileExtension +
                      " > $user_Directory/$stream" + newFileExtension );
                System.out.println( "\t\t\t" + "rm $user_Directory/$stream" +
                      fileExtension );

                System.out.println(
                    "\t\t\techo 'Running batch stream in following sequence:' " );
                System.out.println( "\t\t\tcat $user_Directory/$stream" +
                      newFileExtension + ";;" );

                System.out.println( );
            }
            else
            {
                if ( ( batchKey.getKey( ) != null ) &&
                      !batchStream.equals( batchKey.getKey( ) ) )
                {
                    filter = batchKey.getValue( ).toString( );
                    filter =
                        filter.replace( "[", "" ).replace( "]", "" ).replace(
                            ",", "" );
                    System.out.println( "\t\t\t\"" + batchKey.getKey( ) +
                          "\")" + "\n\t\t\techo \'" + filter +
                          "\' > $user_Directory/$stream" + fileExtension );
                    System.out.println( "\t\t\t" + "tr ' ' " + newLine +
                          " < $user_Directory/$stream" + fileExtension +
                          " > $user_Directory/$stream" + newFileExtension );
                    System.out.println( "\t\t\t" +
                          "rm $user_Directory/$stream" + fileExtension );

                    System.out.println(
                        "\t\t\techo 'Running batch stream in following sequence:' " );
                    System.out.println( "\t\t\tcat $user_Directory/$stream" +
                          newFileExtension + ";;" );

                    System.out.println( );

                    batchStream = batchKey.getKey( );
                }
            }
        }
        System.out.println("\n---------END of FILE---------");
       
        f.close( );
        
        ps.println(
            "\nSuccessfully finised writing in 'Shell_Command_Log.txt' file..." );
    }


    /**
     * <p>
     * This method will parse 'config.txt' line by line and will
     * return jobs and batch stream in sequence.
     * </p>
     * Important parameter needs to be changed:<br>
     *
     * <ul>
     *
     * <li>Location of configuration (from) <br>
     * <li>Assign dedicated batch stream to <b>String Batch_Stream</b>
     * (e.g. String Batch_Stream = "XYZ";)
     * </ul>
     *
     *
     * @throws IOException
     */
    public static void main( String[] args )
      throws IOException
    {
        BufferedReader BATCH_JOB =
            new BufferedReader( new FileReader(
                    "..\\BatchFilter\\src\\batchFilter\\config.txt" ) );

        String Batch_Stream = "XYZ";

        String text_Filter = "SCHEDULE xxworkstation#";
        String filter_Iteration = "SCHEDULE xxworkstation#KILL";
        String HASH = "#";
        String Batch_Key = null;
        String line;
        String functional_Area = "";
        String new_Stream_key = "";

        FileOutputStream f =
            new FileOutputStream(
                "..//BatchFilter//src//batchFilter//Console Log//Not_Processed_Batch Stream_Log.txt" );

        ps.println( "Generating 'Not_Processed_Batch Stream_Log.txt' file..." );

        while ( ( line = BATCH_JOB.readLine( ) ) != null )
        {
            System.setOut( new PrintStream( f ) );

            if ( line.contains( text_Filter ) && !line.equals( "" ) &&
                  !line.contains( filter_Iteration ) )
            {
                Batch_Key =
                    StringUtils.substringBetween( line.trim( ), HASH, " " );
                new_Stream_key = Batch_Key;

                if ( ( Batch_Key != null ) &&
                    Batch_Stream.contains(
                          Batch_Key.substring( 0, Batch_Key.indexOf( "-" ) ) ) )
                {
                    if ( Batch_Key.contains( "-" ) )
                    {
                        functional_Area =
                            Batch_Key.substring( 0, Batch_Key.indexOf( "-" ) );
                    }

                    if ( Batch_Stream.contains( functional_Area.trim( ) ) )
                    {
                        if ( !BATCH_JOBS.containsKey( Batch_Key ) )
                        {
                            BATCH_JOBS.put( Batch_Key,
                                new ArrayList<String>( ) );
                        }
                    }
                }
                else
                {
                    if ( Batch_Key != null )
                    {
                        System.out.println( "Batch not being processed: " +
                              Batch_Key );
                    }
                }
            }
            else
            {
                if ( BATCH_JOBS.containsKey( new_Stream_key ) &&
                      ( line.trim( ).length( ) > 12 ) &&
                      !line.trim( ).contains( "*" ) )
                {
                    String jobId =
                        line.trim( ).substring( 0,
                            line.trim( ).indexOf( " " ) );

                    BATCH_JOBS.get( new_Stream_key ).add( jobId );
                }
            }
        }
        
        System.out.println("---------END of FILE---------");
        
       f.close( );

        ps.println(
            "\nSuccessfully finised writing in 'Not_Processed_Batch Stream_Log.txt' file..." );

        generateShellCommand( BATCH_JOBS );
    }

    //~ Static variables -------------------------------------------------------

    private static PrintStream ps = System.out;
    private static HashMap<String, ArrayList<String>> BATCH_JOBS =
        new HashMap<String, ArrayList<String>>( );
}
