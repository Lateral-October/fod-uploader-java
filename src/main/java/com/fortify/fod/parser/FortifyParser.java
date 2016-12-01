package com.fortify.fod.parser;

import org.apache.commons.cli.*;
import org.apache.commons.cli.CommandLine;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

public class FortifyParser {
    static final String USERNAME = "userCredentials";
    private static final String USERNAME_SHORT = "uc";

    static final String API = "apiCredentials";
    private static final String API_SHORT = "ac";

    static final String ZIP_LOCATION = "zipLocation";
    private static final String ZIP_LOCATION_SHORT = "z";

    static final String BSI_URL = "bsiUrl";
    private static final String BSI_URL_SHORT = "u";

    private static final String HELP = "help";
    private static final String HELP_SHORT = "h";

    private static final String VERSION = "version";
    private static final String VERSION_SHORT = "v";

    public static final String POLLING_INTERVAL = "pollingInterval";
    private static final String POLLING_INTERVAL_SHORT = "I";

    public static final String RUN_SONATYPE_SCAN = "runSonatypeScan";
    private static final String RUN_SONATYPE_SCAN_SHORT = "s";

    public static final String AUDIT_PREFERENCE_ID = "auditPreferenceId";
    private static final String AUDIT_PREFERENCE_ID_SHORT = "a";

    public static final String SCAN_PREFERENCE_ID = "scanPreferenceId";
    private static final String SCAN_PREFERENCE_ID_SHORT = "p";

    static final String PROXY = "proxy";
    private static final String PROXY_SHORT = "P";

    static final String ENTITLEMENT_ID = "entitlementId";
    private static final String ENTITLEMENT_ID_SHORT = "e";

    static final String ENTITLEMENT_FREQUENCY_TYPE = "entitlementFrequency";
    private static final String ENTITLEMENT_FREQUENCY_TYPE_SHORT = "f";

    public static final String IS_REMEDIATION_SCAN = "isRemediationScan";
    private static final String IS_REMEDIATION_SCAN_SHORT = "r";

    public static final String EXCLUDE_THIRD_PARTY_LIBS = "excludeThirdPartyApps";
    private static final String EXCLUDE_THIRD_PARTY_LIBS_SHORT = "x";

    private Options options = new Options();
    private CommandLineParser parser = new DefaultParser();

    /**
     * Argument paring wrapper for the Fod Uploader.
     */
    public FortifyParser() {
        // creates 2 arguments which aren't required. #documentation
        Option help = new       Option(HELP_SHORT, HELP, false, "print this message");
        Option version = new    Option(VERSION_SHORT, VERSION, false, "print the version information and exit");

        // Creates the polling interval argument ( -pollingInterval <<minutes> required=false interval between
        // checking scan status
        Option pollingInterval = Option.builder(POLLING_INTERVAL_SHORT)
                .longOpt(POLLING_INTERVAL)
                .hasArg(true).argName("minutes")
                .desc("interval between checking scan status")
                .required(false).build();

        // Creates the run sonatype scan argument ( -runSonatypeScan <true | false> required=false whether to run a
        // Sonatype Scan
        Option runSonatypeScan = Option.builder(RUN_SONATYPE_SCAN_SHORT)
                .longOpt(RUN_SONATYPE_SCAN)
                .hasArg(true).argName("true|false")
                .desc("whether to run a Sonatype Scan")
                .required(false).build();

        // Creates the audit preference id argument ( -auditPreferenceId <1 | 2> required=false false positive audit
        // type (Manual or Automated) )
        Option auditPreferenceId = Option.builder(AUDIT_PREFERENCE_ID_SHORT)
                .longOpt(AUDIT_PREFERENCE_ID)
                .hasArg(true).argName("1|2")
                .desc("false positive audit type (Manual or Automated)")
                .required(false).build();

        // Creates the scan preference id argument ( -scanPreferenceId <1 | 2> required=false scan mode (Standard or
        // Express) )
        Option scanPreferenceId = Option.builder(SCAN_PREFERENCE_ID_SHORT)
                .longOpt(SCAN_PREFERENCE_ID)
                .hasArg(true).argName("1|2")
                .desc("scan mode (Standard or Express)")
                .required(false).build();

        // Creates the bsi url argument ( -bsiUrl <url> required=true build server url )
        Option bsiUrl = Option.builder(BSI_URL_SHORT)
                .longOpt(BSI_URL)
                .hasArg(true).argName("url")
                .desc("build server url")
                .required(true).build();

        // Creates the zip location argument ( -zipLocation <file> required=true location of scan )
        Option zipLocation = Option.builder(ZIP_LOCATION_SHORT)
                .longOpt(ZIP_LOCATION)
                .hasArg(true).argName("file")
                .desc("location of scan")
                .required(true).build();

        // creates the entitlement id argument ( -entitlementId <id> required=true entitlement id )
        Option entitlementId = Option.builder(ENTITLEMENT_ID_SHORT)
                .longOpt(ENTITLEMENT_ID)
                .hasArg(true).argName("id")
                .desc("entitlement id")
                .required(true).build();

        // creates the entitlement frequency type argument ( -entitlementFrequencyType <id> required=true entitlement frequency type )
        Option entitlementFrequencyType = Option.builder(ENTITLEMENT_FREQUENCY_TYPE_SHORT)
                .longOpt(ENTITLEMENT_FREQUENCY_TYPE)
                .hasArg(true).argName("1|2")
                .desc("entitlement frequency type")
                .required(true).build();

        Option excludeThirdPartyLibs = Option.builder(EXCLUDE_THIRD_PARTY_LIBS_SHORT)
                .longOpt(EXCLUDE_THIRD_PARTY_LIBS)
                .hasArg(true).argName("true|false")
                .desc("whether to exclude third party libraries")
                .required(false).build();

        Option isRemediationScan = Option.builder(IS_REMEDIATION_SCAN_SHORT)
                .longOpt(IS_REMEDIATION_SCAN)
                .hasArg(true).argName("true|false")
                .desc("whether the scan is in remediation")
                .required(false).build();

        // Add the options to the options list
        options.addOption(help);
        options.addOption(version);
        options.addOption(bsiUrl);
        options.addOption(zipLocation);
        options.addOption(pollingInterval);
        options.addOption(runSonatypeScan);
        options.addOption(auditPreferenceId);
        options.addOption(scanPreferenceId);
        options.addOption(entitlementId);
        options.addOption(entitlementFrequencyType);
        options.addOption(isRemediationScan);
        options.addOption(excludeThirdPartyLibs);

        // This one is so dirty I separated it from the rest of the pack.
        // I put all proxy settings into one option with **up to** 5 arguments. Then I do a little cheese
        // for the argName so that it will display with "-help"
        Option proxy = Option.builder(PROXY_SHORT)
                .longOpt(PROXY)
                .hasArgs().numberOfArgs(5).argName("proxyUrl> <username> <password> <ntDomain> <ntWorkstation")
                .desc("credentials for accessing the proxy")
                .required(false).build();
        proxy.setOptionalArg(true);
        options.addOption(proxy);

        // I've put the log-in credentials into a special group to denote that either can be used.
        // Similar build as Proxy, but I won't be using a custom class for these.
        Option username = Option.builder(USERNAME_SHORT)
                .longOpt(USERNAME)
                .hasArg().numberOfArgs(2).argName("username> <password")
                .desc("login credentials")
                .build();
        Option api = Option.builder(API_SHORT)
                .longOpt(API)
                .hasArg().numberOfArgs(2).argName("key> <secret")
                .desc("api credentials")
                .build();

        OptionGroup credentials = new OptionGroup();
        credentials.setRequired(true);
        credentials.addOption(username);
        credentials.addOption(api);

        options.addOptionGroup(credentials);
    }

    /**
     * Gets the various arguments and handles them accordingly.
     * @param args arguments to parse
     */
    public FortifyCommandLine parse(String[] args) {
        try {
            CommandLine cmd = parser.parse(options, args);

            // Put args into an object for easy handling.
            return new FortifyCommandLine(cmd);

        // Throws if username, password, zip location and bsi url aren't all present.
        } catch (ParseException e) {
            // If the user types just -help or just -version, then it will handle that command.
            // Regex is used here since cmd isn't accessible.
            if(args.length > 0) {
                if (Pattern.matches("(-{1,2}("+HELP+"|"+HELP_SHORT+"))", args[0])) {
                    help();
                    return new FortifyCommandLine();
                } else if (Pattern.matches("(-{1,2}("+VERSION+"|"+VERSION_SHORT+"))", args[0])) {
                    version();
                    return new FortifyCommandLine();
                }
            }
            // I can no longer hope to imagine the command you intended.
            System.err.println(e.getMessage());
            System.err.println("try \"-" + HELP + "\" for info");

            return new FortifyCommandLine();
        } catch(Exception e) {
            e.printStackTrace();
            return new FortifyCommandLine();
        }
    }

    /**
     * Displays help dialog.
     */
    private void help() {
        final String header = "FodUpload is a command-line tool for uploading a static scan. \n\nConnect to the api with " +
                "either \"-username\" or \"-api\".";
        final int width = 120;
        final int padding = 5;
        HelpFormatter formatter = new HelpFormatter();
        PrintWriter out = new PrintWriter(System.out, true);

        formatter.setDescPadding(padding);
        formatter.setOptionComparator(HelpComparator);

        formatter.printWrapped(out, width, header);
        formatter.printUsage(out, width, "FodUpload.jar", options);
        formatter.printWrapped(out, width, ""); // New line
        formatter.printOptions(out, width, options, formatter.getLeftPadding(), formatter.getDescPadding());
    }

    private void version() {
        Package p = getClass().getPackage();
        System.out.println("Version " + p.getImplementationVersion());
    }
    /**
     * Compares options so that they are ordered:
     * 1.) by required, then by
     * 2.) short operator.
     * Used for sorting the results of the Help command.
     */
    private static Comparator<Option> HelpComparator = new Comparator<Option>() {
        @Override
        public int compare(Option o1, Option o2) {
            String required1 = o1.isRequired() ? "1" : "0";
            String required2 = o2.isRequired() ? "1" : "0";

            int result = required2.compareTo(required1);
            if (result == 0) {
                // will try to sort by short Operator but if it doesn't exist then it'll use long operator
                String comp1 = o1.getOpt() == null ? o1.getLongOpt() : o1.getOpt();
                String comp2 = o2.getOpt() == null ? o2.getLongOpt() : o2.getOpt();

                result = comp1.compareToIgnoreCase(comp2);
            }
            return result;
        }
    };
}
