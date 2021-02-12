package cn.edu.sustech.cse.sqlab.leakdroid.stages.dex2Jar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import com.googlecode.d2j.dex.Dex2jar;
import com.googlecode.d2j.reader.DexFileReader;
import com.googlecode.d2j.reader.zip.ZipUtil;
import com.googlecode.dex2jar.tools.BaksmaliBaseDexExceptionHandler;
import org.apache.log4j.Logger;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 19:20
 */
public class Dex2Jar2_0 extends Dex2Jar {
    private final static Logger logger = Logger.getLogger(Dex2Jar2_0.class);

    @Override
    public File convert2Jar(File apkFile, File outputFolder) throws IOException {
        File outputJarFile = Paths.get(outputFolder.getAbsolutePath(), String.format("%s_d2j.jar", apkFile.getName())).toFile();
        DexFileReader reader = new DexFileReader(ZipUtil.readDex(apkFile));
        BaksmaliBaseDexExceptionHandler exceptionHandler = new BaksmaliBaseDexExceptionHandler();
        Dex2jar.from(reader)
                .withExceptionHandler(exceptionHandler)
                .reUseReg(false)
                .topoLogicalSort()
                .skipDebug(true)
                .optimizeSynchronized(false)
                .to(outputJarFile.toPath());

        OptionsArgs.setConvertedJarFile(outputJarFile);
        logger.info(String.format("Apk file is converted to %s", outputJarFile));
        return outputJarFile;
    }
}
