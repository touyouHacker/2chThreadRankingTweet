package jp.co.touyouhk.nichannel.subjecttext;

import java.util.Arrays;
import java.util.List;

import jp.co.touyouhk.nichannel.subjecttext.SubjectTextEntity;
import jp.co.touyouhk.nichannel.subjecttext.SubjectTextUtil;
import junit.framework.*;


public class SubjectTextUtilTest extends TestCase {

	
	public void testTreadNumber2datURL(){
		
		String datURL = SubjectTextUtil.treadNumber2datURL("http://kamome.2ch.net/magazin/", "1316691172");
		assertEquals("http://kamome.2ch.net/test/read.cgi/magazin/1316691172/" , datURL);
		
	}
	
	// subjectText2Entityの動作確認、assertはなし目視確認
	public void testSubjectText2Entity(){
		
		String testSubject = "./test/jp/co/touyouhk/niChannel/subjecttext/testdata/subjectOld.txt";
		
		List<SubjectTextEntity> list = SubjectTextUtil.subjectText2Entity(testSubject);
		
		System.out.println(" ------------------ Start ----------------------");
		for(SubjectTextEntity ent: list){
			System.out.print(ent.getThreadNumber() + " " + ent.getTitle() + " " +
					ent.getThreadResCount() + " " + ent.getDeleteFlag() + " " + ent.getResInterval());
			System.out.println();
		}
		System.out.println(" ------------------ END ------------------------");
		
	}
	
	
	// subjectText2Entityとエンティティソートの動作確認、パターン01 全部勢い0
	public void testSubjectText2EntityAndSortPatarn01(){
		
		String testSubject = "./test/jp/co/touyouhk/niChannel/subjecttext/testdata/subjectOld.txt";
		
		List<SubjectTextEntity> list = SubjectTextUtil.subjectText2Entity(testSubject);
		
		SubjectTextEntity[] array = list
		.toArray(new SubjectTextEntity[list.size()]);
		Arrays.sort(array);
		List <SubjectTextEntity> sortList = Arrays.asList(array);
		

		/*
		for(SubjectTextEntity ent: sortList){
			System.out.print(ent.getThreadNumber() + " " + ent.getTitle() + " " +
					ent.getThreadResCount() + " " + ent.getDeleteFlag() + " " + ent.getResInterval());
			System.out.println();
		}
		*/
		
		//最初のスレ番号
		assertEquals(sortList.get(0).getThreadNumber(), "1316957824");
		
		//最後のスレ番号
		assertEquals(sortList.get(sortList.size()-1).getThreadNumber(), "1316659361" );
		
		//スレの総数
		assertEquals(sortList.size(), list.size());
		
		assertEquals(list, sortList);
	}
	
	
	// subjectText2Entityとエンティティソートの動作確認、パターン02 勢い=レス数
	public void testSubjectText2EntityAndSortPatarn02() {

		String testSubject = "./test/jp/co/touyouhk/niChannel/subjecttext/testdata/subjectOld.txt";

		List<SubjectTextEntity> list = SubjectTextUtil
				.subjectText2Entity(testSubject);

		for (SubjectTextEntity ent : list) {

			ent.setResInterval(ent.getThreadResCount());
		}

		SubjectTextEntity[] array = list.toArray(new SubjectTextEntity[list
				.size()]);
		Arrays.sort(array);
		List<SubjectTextEntity> sortList = Arrays.asList(array);

		for (SubjectTextEntity ent : sortList) {
			System.out.print(ent.getThreadNumber() + " " + ent.getTitle() + " "
					+ ent.getThreadResCount() + " " + ent.getDeleteFlag() + " "
					+ ent.getResInterval());
			System.out.println();
		}

		for (SubjectTextEntity ent : list) {

			for (SubjectTextEntity sortEnt : sortList) {

				if (ent.getThreadNumber().equals(sortEnt.getThreadNumber())) {
					assertEquals(ent, sortEnt);
				}
			}

		}

		// スレの総数
		assertEquals(sortList.size(), list.size());

	}
	
	//generateTreadRankingメソッドのテスト（目視）
	public void testGenerateTreadRanking(){
		
		String oldSubject = "./test/jp/co/touyouhk/niChannel/subjecttext/testdata/subjectOld.txt";
		String newSubject = "./test/jp/co/touyouhk/niChannel/subjecttext/testdata/subjectNew.txt";
		
		List<SubjectTextEntity> oldList = SubjectTextUtil
				.subjectText2Entity(oldSubject);
		
		List<SubjectTextEntity> newList = SubjectTextUtil
		.subjectText2Entity(newSubject);
		
		
		List<SubjectTextEntity> rankList = SubjectTextUtil.generateTreadRanking(oldList, newList);
	
		for (SubjectTextEntity ent : rankList) {
			System.out.print(ent.getThreadNumber() + " " + ent.getTitle() + " "
					+ ent.getThreadResCount() + " " + ent.getDeleteFlag() + " "
					+ ent.getResInterval());
			System.out.println();
		}
	}
}
