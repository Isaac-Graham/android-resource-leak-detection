import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsParser;
import cn.edu.sustech.cse.sqlab.leakdroid.exceptions.ParseOptionsException;
import org.junit.Test;

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

    @Test
    public void test_success() {
        String[] args = {"-i",
                "C:\\Users\\Isc\\Desktop\\AnkiDroid-rev-3e9ddc7eca.apk",
                "-a",
                String.format("%s\\platforms", System.getenv("ANDROID_HOME"))};
        run(args);
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
