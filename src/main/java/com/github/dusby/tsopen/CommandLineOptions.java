package com.github.dusby.tsopen;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class sets the different option for the application
 * @author Jordan Samhi
 *
 */
public class CommandLineOptions {

	private static final Triplet<String, String, String> FILE = new Triplet<String, String, String>("file", "f", "Apk file");
	private static final Triplet<String, String, String> HELP = new Triplet<String, String, String>("help", "h", "Print this message");
	private static final Triplet<String, String, String> TIMEOUT =
			new Triplet<String, String, String>("timeout", "t", "Set a timeout in minutes (60 by default) to exit the application");
	private static final Triplet<String, String, String> PLATFORMS =
			new Triplet<String, String, String>("platforms", "p", "Android platforms folder");
	private static final Triplet<String, String, String> EXCEPTIONS =
			new Triplet<String, String, String>("exceptions", "e", "Take exceptions into account during full path predicate recovery");
	private static final String TSOPEN = "TSOpen";

	private Options options, firstOptions;
	private CommandLineParser parser;
	private CommandLine cmdLine, cmdFirstLine;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommandLineOptions(String[] args) {
		this.options = new Options();
		this.firstOptions = new Options();
		this.initOptions();
		this.parser = new DefaultParser();
		this.parse(args);
	}

	/**
	 * This method does the parsing of the arguments.
	 * It distinguished, real options and help option.
	 * @param args the arguments of the application
	 */
	private void parse(String[] args) {
		HelpFormatter formatter = null;
		try {
			this.cmdFirstLine = this.parser.parse(this.firstOptions, args, true);
			if (this.cmdFirstLine.hasOption(HELP.getValue0())) {
				formatter = new HelpFormatter();
				formatter.printHelp(TSOPEN, this.options, true);
				System.exit(0);
			}
			this.cmdLine = this.parser.parse(this.options, args);
		} catch (ParseException e) {
			this.logger.error(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Initialization of all recognized options
	 */
	private void initOptions() {
		final Option file = Option.builder(FILE.getValue1())
				.longOpt(FILE.getValue0())
				.desc(FILE.getValue2())
				.hasArg(true)
				.argName(FILE.getValue0())
				.required(true)
				.build();

		final Option platforms = Option.builder(PLATFORMS.getValue1())
				.longOpt(PLATFORMS.getValue0())
				.desc(PLATFORMS.getValue2())
				.hasArg(true)
				.argName(PLATFORMS.getValue0())
				.required(true)
				.build();

		final Option help = Option.builder(HELP.getValue1())
				.longOpt(HELP.getValue0())
				.desc(HELP.getValue2())
				.argName(HELP.getValue0())
				.build();

		final Option exceptions = Option.builder(EXCEPTIONS.getValue1())
				.longOpt(EXCEPTIONS.getValue0())
				.desc(EXCEPTIONS.getValue2())
				.argName(EXCEPTIONS.getValue0())
				.build();

		final Option timeout = Option.builder(TIMEOUT.getValue1())
				.longOpt(TIMEOUT.getValue0())
				.desc(TIMEOUT.getValue2())
				.argName(TIMEOUT.getValue0())
				.hasArg(true)
				.build();
		timeout.setOptionalArg(true);
		timeout.setType(Number.class);

		this.firstOptions.addOption(help);

		this.options.addOption(file);
		this.options.addOption(platforms);
		this.options.addOption(exceptions);
		this.options.addOption(timeout);
		for(Option o : this.firstOptions.getOptions()) {
			this.options.addOption(o);
		}
	}

	public String getFile() {
		return this.cmdLine.getOptionValue(FILE.getValue0());
	}

	public String getPlatforms() {
		return this.cmdLine.getOptionValue(PLATFORMS.getValue0());
	}

	public boolean hasExceptions() {
		return this.cmdLine.hasOption(EXCEPTIONS.getValue1());
	}

	public int getTimeout() {
		Number n = null;
		try {
			n = (Number)this.cmdLine.getParsedOptionValue(TIMEOUT.getValue1());
			if(n == null) {
				return 0;
			}else {
				return n.intValue();
			}
		} catch (Exception e) {}
		return 0;
	}
}
