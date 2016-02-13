package com.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.decisiontree.common.DecisionNode;
import com.decisiontree.common.FeatureCounts;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
        
		DecisionNode<Integer> root=new DecisionNode<Integer>(new FeatureCounts<Integer>("Teemp"));
		DecisionNode<Integer> child1=new DecisionNode<Integer>(new FeatureCounts<Integer>("Teemp"));
		DecisionNode<Integer> child2=new DecisionNode<Integer>(new FeatureCounts<Integer>("Teemp"));

		DecisionNode<Integer> child23=new DecisionNode<Integer>(new FeatureCounts<Integer>("Teemp"));
		child2.setChildren(Collections.singletonList(child23));
		List<DecisionNode<Integer>> list = new ArrayList<DecisionNode<Integer>>();
		list.add(child1);
		list.add(child2);
		root.setChildren(list);
		int count = new ID3Implementation().getNumNonLeafNodes(root);
		assertEquals(count, 2);
		
    }
}
