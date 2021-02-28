import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsParser;
import cn.edu.sustech.cse.sqlab.leakdroid.exceptions.ParseOptionsException;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/27 0:07
 */
public class OptionsTest {

    private void run(String[] args) {
        OptionsParser.parse(args);
        OptionsArgs.initialOptions();
    }


    @Test(expected = ParseOptionsException.class)
    public void test_invalid_android_path() {
        String[] args = {"-i",
                "C:\\Users\\Isc\\Desktop\\github-benchmark.apk",
                "-a",
                "invalid"};
        run(args);
    }


    @Test(expected = ParseOptionsException.class)
    public void test_invalid_input_path() {
        String[] args = {"-i",
                "invalid",
                "-a",
                String.format("%s\\platforms", System.getenv("ANDROID_HOME"))};
        run(args);
    }
}
