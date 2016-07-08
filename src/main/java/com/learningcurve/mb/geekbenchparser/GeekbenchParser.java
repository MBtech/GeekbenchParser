/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learningcurve.mb.geekbenchparser;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Program to return the numbers from Geekbench 3 run for programmatic use.
 * Args: takes the URL that is provided at the end geekbench run Returns: a
 * HashMap containing 3 entries. Each entry is a HashMap with several subsequent
 * entries containing results for the benchmarks in each category Entry 1:
 * Integer Performance Entry 2: Floating Point Performance Entry 3: Memory
 * Performance
 *
 * @author Muhammad Bilal
 */
public class GeekbenchParser {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println(args[0]);
        String url = args[0];
        File urlFile = new File("urls");
        FileWriter fw = new FileWriter(urlFile,true);
        fw.write(url);
        fw.close();
        Document doc = Jsoup.connect(url).get();
        HashMap<String, HashMap<String, String>> results = parseBenchmarkInfo(doc);

        for (String cat : results.keySet()) {
            File file = new File(cat + "_results.csv");
            boolean headerFlag = !file.exists();
            CSVWriter writer = new CSVWriter(new FileWriter(cat + "_results.csv",true));
            Set<String> keys = results.get(cat).keySet();
            System.out.println(keys);
            String[] header = keys.toArray(new String[keys.size()]);
            if (headerFlag) {
                writer.writeNext(header);
            }
            ArrayList<String> vals = new ArrayList<>();
            for (int i = 0; i < header.length; i++) {
                vals.add(results.get(cat).get(header[i]));
            }
            String[] values = vals.toArray(new String[vals.size()]);
            writer.writeNext(values);
            writer.close();
        }

    }

    public static HashMap<String, HashMap<String, String>> parseBenchmarkInfo(Document doc) {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        String[] perfBenchmark = {"Integer Performance", "Floating Point Performance", "Memory Performance"};

        Elements benchmarks = doc.select("div.span9").select("table.table.section-performance");

        for (int i = 0; i < 3; i++) {
            Elements perf = benchmarks.get(i)
                    .select("tr").select("td.score");
            Elements perfTitle = benchmarks.get(i)
                    .select("tr").select("td.name");
            Iterator<Element> score = perf.iterator();
            Iterator<Element> title = perfTitle.iterator();
            HashMap<String, String> temp = new HashMap<>();
            while (title.hasNext() && score.hasNext()) {
                String next = score.next().text();
                String num = next.split(" ")[1].concat(" " + next.split(" ")[2]);
                String name = title.next().text();
                temp.put(name, num);
                //System.out.println(name + " : " + num);
            }
            result.put(perfBenchmark[i], temp);
        }
        return result;
    }
}
