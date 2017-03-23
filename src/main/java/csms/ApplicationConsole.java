package csms;

import csms.core.JhAction;
import csms.core.JhActionList;
import csms.core.JhFile;
import csms.core.JhFileStructure;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicationConsole {

    public static void main(String[] args) {


        //**************************************************************************
        //Testing JhFile equals method
        LocalDateTime time1 = LocalDateTime.of(2017, 04, 21, 12, 20, 30);
        LocalDateTime time2 = LocalDateTime.of(2017, 04, 21, 12, 22, 30);
        LocalDateTime time3 = LocalDateTime.of(2017, 04, 21, 12, 27, 30);
        JhFile file1 = new JhFile("haza.txt", time1, "dropbox1", false);
        JhFile file2 = new JhFile("haza.txt", time2, "gdrive1", false);
        JhFile file3 = new JhFile("haza.txt", time3, "onedrive1", false);

        System.out.println(file1.equalsInTimeRange(file2));
        System.out.println(file1.equalsInTimeRange(file3));
        System.out.println(file1.equalsInTimeRange(file1));

        //Expecting to see: true false true

        //*****************************************************************************
        //Testing File Structure Merge Functionality
        LocalDateTime time1_1 = LocalDateTime.of(2017, 04, 21, 12, 20, 30);//Same as time1_2
        LocalDateTime time2_1 = LocalDateTime.of(2017, 04, 21, 13, 29, 30);//Greater than time2_2
        LocalDateTime time3_1 = LocalDateTime.of(2017, 04, 21, 14, 11, 30);//Lesser than time3_2
        LocalDateTime time1_2 = LocalDateTime.of(2017, 04, 21, 12, 21, 30);
        LocalDateTime time2_2 = LocalDateTime.of(2017, 04, 21, 13, 22, 30);
        LocalDateTime time3_2 = LocalDateTime.of(2017, 04, 21, 14, 20, 30);
        JhFile strucFile1 = new JhFile("identical.txt", time1_1, "base", false);
        JhFile strucFile2 = new JhFile("test2.txt", time2_1, "base", false);
        JhFile strucFile3 = new JhFile("test3.txt", time3_1, "base", false);
        JhFile strucFile4 = new JhFile("file2Delete.txt", time1_1, "base", false);
        JhFile strucFile5 = new JhFile("identical.txt", time1_2, "gdrive1", false);
        JhFile strucFile6 = new JhFile("test2.txt", time2_2, "gdrive1", false);
        JhFile strucFile7 = new JhFile("test3.txt", time3_2, "gdrive1", false);
        JhFile strucFile8 = new JhFile("newGDriveFile.txt", time1_1, "gdrive1", false);
        JhFile strucFile9 = new JhFile("newDropBoxFile.txt", time1_1, "dropbox1", false);

        JhFileStructure baseFS = new JhFileStructure(strucFile1,strucFile2,strucFile3,strucFile4);
        JhFileStructure gDriveFS = new JhFileStructure(strucFile5,strucFile6,strucFile7,strucFile8);
        JhFileStructure dropBoxFS = new JhFileStructure(strucFile1,strucFile2,strucFile3,strucFile4,strucFile9);

        baseFS.mergeFileStructures(gDriveFS);
        baseFS.mergeFileStructures(dropBoxFS);
        System.out.println("***************Testing File Structure Merge Functionality********************************");
        //identical.txt: "deleteCandidate":false, "newFile":false, "mainSourceDriveId":"base"
        //test2.txt: "deleteCandidate":false, "newFile":false, "mainSourceDriveId":"base"
        //test3.txt: "deleteCandidate":false, "newFile":false, "mainSourceDriveId":"gdrive1"
        //file2Delete.txt: "deleteCandidate":true, "newFile":false, "mainSourceDriveId":"base"
        //newGDriveFile.txt: "deleteCandidate":false, "newFile":true, "mainSourceDriveId":"gdrive1"
        //newDropBoxFile.txt: "deleteCandidate":false, "newFile":true, "mainSourceDriveId":"dropbox1"
        System.out.println(baseFS);

        JhActionList actions1 = gDriveFS.mergeFileStructures(baseFS);
        System.out.println("****************Testing Actions List: 1-2 drives. GDrive*********************************");
        //UPDATE - test2.txt - base
        //CREATE - newDropBoxFile.txt - dropbox1
        System.out.println(actions1);

        JhActionList actions2 = dropBoxFS.mergeFileStructures(baseFS);
        System.out.println("****************Testing Actions List: 2-2 drives. DropBox********************************");
        //UPDATE - test3.txt - gdrive1
        //CREATE - newGDriveFile.txt - gdrive1
        System.out.println(actions2);
    }

}
