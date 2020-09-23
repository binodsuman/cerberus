/*
 * Copyright of Koninklijke Philips N.V. 2020
 */

package com.philips.swcoe.cerberus.hounds;

import static com.philips.swcoe.cerberus.constants.DescriptionConstants.FILES_CMD_LINE_OPTION_DESCRIPTION;
import static com.philips.swcoe.cerberus.constants.DescriptionConstants.FILES_OPTION_NOT_NULL_ARGUMENT_MESSAGE;
import static com.philips.swcoe.cerberus.constants.DescriptionConstants.LANGUAGE_CMD_LINE_OPTION_DESCRIPTION;
import static com.philips.swcoe.cerberus.constants.DescriptionConstants.LANGUAGE_OPTION_NOT_NULL_ARGUMENT_MESSAGE;
import static com.philips.swcoe.cerberus.constants.DescriptionConstants.MINIMUM_TOKEN_CMD_LINE_OPTION_DESCRIPTION;
import static com.philips.swcoe.cerberus.constants.DescriptionConstants.MINIMUM_TOKEN_OPTION_NOT_NULL_ARGUMENT_MESSAGE;
import static com.philips.swcoe.cerberus.constants.DescriptionConstants.REPORT_FORMAT_CMD_LINE_OPTION_DESCRIPTION;
import static com.philips.swcoe.cerberus.constants.DescriptionConstants.REPORT_FORMAT_OPTION_NOT_NULL_ARGUMENT_MESSAGE;
import static com.philips.swcoe.cerberus.constants.DescriptionConstants.EXCLUSION_DESCRIPTION;
import static com.philips.swcoe.cerberus.constants.ProgramConstants.EXCLUDE;
import static com.philips.swcoe.cerberus.constants.ProgramConstants.COPY_PASTE_DETECTOR;
import static com.philips.swcoe.cerberus.constants.ProgramConstants.FILES_OPTION;
import static com.philips.swcoe.cerberus.constants.ProgramConstants.FORMAT_OPTION;
import static com.philips.swcoe.cerberus.constants.ProgramConstants.LANGUAGE_OPTION;
import static com.philips.swcoe.cerberus.constants.ProgramConstants.MINIMUM_TOKENS_OPTION;
import static net.sourceforge.pmd.cpd.CPDCommandLineInterface.addSourceFilesToCPD;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import com.beust.jcommander.JCommander;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import picocli.CommandLine;

@CommandLine.Command(name = COPY_PASTE_DETECTOR, mixinStandardHelpOptions = true, description = "Detect duplicated blocks of code in your source code")
public class Duplicates extends BaseCommand implements Callable<Integer> {

    @NotNull(message = FILES_OPTION_NOT_NULL_ARGUMENT_MESSAGE)
    @CommandLine.Option(names = FILES_OPTION, description = FILES_CMD_LINE_OPTION_DESCRIPTION)
    private String pathToSource;

    @NotNull(message = REPORT_FORMAT_OPTION_NOT_NULL_ARGUMENT_MESSAGE)
    @CommandLine.Option(names = FORMAT_OPTION, description = REPORT_FORMAT_CMD_LINE_OPTION_DESCRIPTION)
    private String reportFormat;

    @NotNull(message = MINIMUM_TOKEN_OPTION_NOT_NULL_ARGUMENT_MESSAGE)
    @CommandLine.Option(names = MINIMUM_TOKENS_OPTION, description = MINIMUM_TOKEN_CMD_LINE_OPTION_DESCRIPTION)
    private String minimumTokens;

    @NotNull(message = LANGUAGE_OPTION_NOT_NULL_ARGUMENT_MESSAGE)
    @CommandLine.Option(names = LANGUAGE_OPTION, description = LANGUAGE_CMD_LINE_OPTION_DESCRIPTION)
    private String languageOfSource;

    @CommandLine.Option(names = EXCLUDE, description = EXCLUSION_DESCRIPTION)
    private String exclusionFiles;

    @Override
    public Integer call() throws Exception {
        this.validate();
        CPDConfiguration arguments = new CPDConfiguration();
        JCommander jcommander = new JCommander(arguments);
        String excludeParameter = null;
        if (!StringUtils.isEmpty(exclusionFiles)) {
            excludeParameter = EXCLUDE;
        }
        String[] argumentsOfCPD = {
            FILES_OPTION, pathToSource,
            FORMAT_OPTION, reportFormat,
            MINIMUM_TOKENS_OPTION, minimumTokens,
            LANGUAGE_OPTION, languageOfSource,
            excludeParameter, exclusionFiles
        };

        jcommander.parse(Arrays.stream(argumentsOfCPD).filter(Objects::nonNull).toArray(String[]::new));
        arguments.postContruct();
        CPD cpd = new CPD(arguments);
        addSourceFilesToCPD(cpd, arguments);
        cpd.go();

        arguments.getCPDRenderer().render(cpd.getMatches(),
                new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8)));

        return 0;
    }

}
