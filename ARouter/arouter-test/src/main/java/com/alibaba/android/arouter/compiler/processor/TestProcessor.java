package com.alibaba.android.arouter.compiler.processor;


import com.google.auto.service.AutoService;

import org.apache.commons.collections4.MapUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


/**
 * Base Processor
 *
 * @author zhilong [Contact me.](mailto:zhilong.lzl@alibaba-inc.com)
 * @version 1.0
 * @since 2019-03-01 12:31
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.alibaba.android.arouter.facade.annotation.Route"})
public class TestProcessor extends AbstractProcessor {
    Filer mFiler;
    Types types;
    Elements elementUtils;
    // Module name, maybe its 'app' or others
    String moduleName = null;
    // If need generate router doc
    boolean generateDoc;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "======================= " + this.getClass().getName() + " ======================= ");

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get("AROUTER_MODULE_NAME");
        } else {

        }
        for (Map.Entry<String, String> entry : options.entrySet()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "======== key:" + entry.getKey() + "   value:" + entry.getValue());
        }

        if (moduleName == null || moduleName.isBlank()) {
            throw new RuntimeException("There is no module name :" + this.getClass().getName());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>() {{
            this.add("AROUTER_MODULE_NAME");
        }};
    }
}
