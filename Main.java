import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

	static int numRows=64;
	static int numColumns=64;
	static int maxDepth = 4;

	int codeCount =0;

	static final String baseFolder = "/home/abhishek/mmc/";

	public static void main(String [] args){
		Main demo = new Main();
		ArrayList<Integer> mat1 = demo.loadCSV(baseFolder+"mymat_1.csv");
		ArrayList<Integer> mat2 = demo.loadCSV(baseFolder+"mymat_2.csv");
		ArrayList<Integer> mat3 = demo.loadCSV(baseFolder+"mymat_3.csv");
		ArrayList<Entry> input = new ArrayList<Entry>();

		Entry max =new Entry(0,0,0,-1,-1);
		Entry min =new Entry(255,255,255,-1,-1);

		for (int i =0 ; i < numRows; i++)
			for(int j =0 ; j < numColumns; j++){
				int idx = numColumns* i +j;
				Entry entry = new Entry(mat1.get(idx), mat2.get(idx), mat3.get(idx), i, j);
				for(int k =0; k < 3; k++){
					if(max.color[k]< entry.color[k])
						max.color[k] = entry.color[k];
					if(min.color[k]> entry.color[k])
						min.color[k] = entry.color[k];
				}

				input.add(entry);
			}


		ArrayList<Entry> output = demo.doMedianCut(input, min, max, 0);
		demo.saveCSVs(output, baseFolder+"output");
	}

	/**
	 * Main recursion function - ad maxDepth emit the range and normalize to lookup entry
	 * For now lookup is not the median but the mid of the range
	 * TODO - XXX Sagar - add logic for median 
	 * @param input - the part of applicable input - gets halved for each recursion
	 * @param min - input change for color at this level - min
	 * @param max - input range for color at this level - max
	 * @param depth - how many bits used up so far
	 * @return modified colors with mean values - merged from sub recursion calls
	 */
	ArrayList<Entry> doMedianCut(ArrayList<Entry> input, Entry min, Entry max, int depth) {
		System.out.println("\ndoMedianCut:" + input.size() + " : " + 
				min.color[0] + " " +  min.color[1] + " " +  min.color[2] + " : " + 
				max.color[0] + " " +  max.color[1] + " " +  max.color[2] + " : " + depth); 
		
		if(depth == maxDepth) {
			ArrayList<Entry>retVal = new ArrayList<Entry>();
			int[] c = new int[3];
			System.out.print("\n" + codeCount++ + 
					" : " + min.color[0] + " " +  min.color[1] + " " +  min.color[2] + 
					" : " + max.color[0] + " " +  max.color[1] + " " +  max.color[2] + " : ");
			for(int i=0; i < 3; i++){
				c[i] = (min.color[i]+max.color[i])/2;
				System.out.print(c[i] + " ");
			}

			for(Entry entry: input){
				for(int i=0; i < 3; i++){
					entry.color[i] = c[i];
					retVal.add(entry);
				}
			}
			return retVal;
		}

		/* depth % 3 gives which of color 0, 1, 2 is to be used */
		int colorIdx = depth % 3;
		depth++;
		ArrayList<Integer> values = new ArrayList<Integer>();
		for(Entry entry: input){
			values.add(entry.color[colorIdx]);
		}
		Collections.sort(values);
		int median = values.get(values.size()/2);
		ArrayList<Entry> leftHalf = new ArrayList<Entry>();
		ArrayList<Entry> rightHalf = new ArrayList<Entry>();
		for(Entry entry: input){
			if(entry.color[colorIdx]> median)
				rightHalf.add(entry);
			else
				leftHalf.add(entry);
		}
		Entry leftMax = new Entry(max);
		Entry rightMin = new Entry(min);
		leftMax.color[colorIdx] = median;
		rightMin.color[colorIdx] = median;
		ArrayList<Entry> newLeftHalf = doMedianCut(leftHalf, min, leftMax, depth);
		ArrayList<Entry> newRightHalf = doMedianCut(rightHalf, rightMin, max, depth);
		newLeftHalf.addAll(newRightHalf);
		return newLeftHalf;

	}

	private void saveCSVs(ArrayList<Entry> output, String baseName){
		try {
			File[] files = new File[3];
			FileWriter[] writers = new FileWriter[3];
			for(int k=0; k < 3; k++){
				files[k] = new File(baseName + "_" + k + ".csv");
				files[k].createNewFile();
				writers[k] = new FileWriter(files[k]);
			}
			for(int i=0; i < numRows; i++){
				String[] lines = new String[3];
				for(int k=0; k < 3; k++) lines[k] = new String("");
				for(int j=0; j < numColumns; j++){
					Entry e = getEntry(i,j,output);
					for(int k=0; k < 3; k++){
						lines[k] = lines[k].concat(e.color[k] + ",");
					}
				}
				for(int k=0; k < 3; k++){
					int len = lines[k].length();
					lines[k] = lines[k].substring(0, len-2) + "\n";
					writers[k].write(lines[k]);
				}

			}
			for(int k=0; k < 3; k++) {
				writers[k].flush();
				writers[k].close();
			}
		}catch (Exception ex){
			System.err.println(ex);
			System.err.println(ex.getStackTrace().toString());
		}
	}

	private Entry getEntry(int x, int y, ArrayList<Entry> reference){
		for(Entry e: reference){
			if(e.x == x && e.y == y)
				return e;
		}
		return null;
	}
	private ArrayList<Integer> loadCSV(String fileName){
		ArrayList<Integer> retVal = new ArrayList<Integer>();
		try {
			File file = new File(fileName);
			List<String> lines = Files.readAllLines(file.toPath(), 
					StandardCharsets.UTF_8);
			for (String line : lines) {
				String[] array = line.split(",");
				for(String val : array){
					retVal.add(Integer.parseInt(val));
				}
			}
		} catch (Exception ex){
			System.err.println("Exception " + ex + "for file" + fileName);
		}
		return retVal;
	}
}
