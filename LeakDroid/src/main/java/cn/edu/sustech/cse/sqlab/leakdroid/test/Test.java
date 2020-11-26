package cn.edu.sustech.cse.sqlab.leakdroid.test;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.CommandOptions;
import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import org.apache.log4j.Logger;
import soot.Body;
import soot.BodyTransformer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/26 20:45
 */
@PhaseName(name = "stp.test")
public class Test extends BodyTransformer {
    private static int count = 0;
    private static final Logger logger = Logger.getLogger(Test.class);

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> map) {
        List<Pattern> patterns = new LinkedList<>();
        CommandOptions.excludedPackageNames.forEach(packageName -> {
            patterns.add(Pattern.compile(packageName));
        });
        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            Matcher matcher = pattern.matcher(body.getMethod().getDeclaringClass().toString());
            if (matcher.matches()) {
                return;
            }
        }
        String bodyFileName = String.format("./target/body/body%d.txt", count++);
        try (FileWriter fileWriter = new FileWriter(bodyFileName)) {
            fileWriter.write(body.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
