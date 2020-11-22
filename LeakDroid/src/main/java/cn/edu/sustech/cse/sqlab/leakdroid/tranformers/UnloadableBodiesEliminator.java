package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.*;
import soot.shimple.Shimple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/22 22:59
 */
public class UnloadableBodiesEliminator extends SceneTransformer {
    private static Logger logger = Logger.getLogger(UnloadableBodiesEliminator.class);

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        logger.info(String.format("Trying to retrieve bodies from %s application classes in scene", Scene.v().getApplicationClasses().size()));
        Iterator<SootClass> classesIterator = Scene.v().getClasses().snapshotIterator();
        List<SootMethod> methods = new ArrayList<>();
        while (classesIterator.hasNext()) {
            methods.addAll(classesIterator.next().getMethods());
        }

        List<List<SootMethod>> chunkedMethods = Lists.partition(methods, Runtime.getRuntime().availableProcessors());
        chunkedMethods.parallelStream().forEach(methodChunk -> {
            methodChunk.stream().filter(SootMethod::isConcrete).forEach(it -> {
                try {
                    it.retrieveActiveBody();
                } catch (RuntimeException ex) {
                    logger.warn(String.format("Failed to load %s, setting its body to empty", it.toString()));
                    it.setActiveBody(createDummyBody(it));
                }
            });
        });

        logger.info(String.format("Bodies of %s application classes retrieved", Scene.v().getApplicationClasses().size()));
    }

    private static Body createDummyBody(SootMethod method) {
        return Shimple.v().newBody(method);
    }
}
