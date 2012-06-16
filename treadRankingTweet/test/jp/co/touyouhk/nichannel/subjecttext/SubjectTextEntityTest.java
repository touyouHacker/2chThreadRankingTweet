package jp.co.touyouhk.nichannel.subjecttext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.touyouhk.nichannel.subjecttext.SubjectTextEntity;
import junit.framework.TestCase;


public class SubjectTextEntityTest  extends TestCase{
	
	public void testSort(){
		
		List <SubjectTextEntity> list = new ArrayList<SubjectTextEntity>();
		
		SubjectTextEntity e1 = new SubjectTextEntity();
		e1.setResInterval(10);
		e1.setThreadResCount(989);
		e1.setThreadNumber("9999911111");
		
		SubjectTextEntity e2 = new SubjectTextEntity();
		e2.setResInterval(30);
		e2.setThreadResCount(11);
		e2.setThreadNumber("1111122222");
		
		SubjectTextEntity e3 = new SubjectTextEntity();
		e3.setResInterval(20);
		e3.setThreadResCount(1);
		e3.setThreadNumber("7777711111");
		
		list.add(e1);
		list.add(e2);
		list.add(e3);
		
		//参考:http://plaza.rakuten.co.jp/koh05/diary/201007030000/
		
		SubjectTextEntity [] array =  list.toArray(new SubjectTextEntity [list.size()]);
		
	    Arrays.sort(array);
	    
	    assertEquals(30, array[0].getResInterval());
	    assertEquals(11, array[0].getThreadResCount());
	    
	    assertEquals(20, array[1].getResInterval());
	    assertEquals(1, array[1].getThreadResCount());
	    
	    assertEquals(10, array[2].getResInterval());
	    assertEquals(989, array[2].getThreadResCount());
	    
	}
	

}
