package com.github.dusby;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.javatuples.Triplet;

public class CommandLineOptions {
	
	private static final Triplet<String, String, String> FILE = new Triplet<String, String, String>("file", "f", "Apk file");
	private static final Triplet<String, String, String> HELP = new Triplet<String, String, String>("help", "h", "Print this message");
	private static final Triplet<String, String, String> PLATFORMS = new Triplet<String, String, String>("platforms", "p", "Android platforms folder");
	private static final String TSOPEN = "TSOpen";
	
	private Options options, firstOptions;
	private CommandLineParser parser;
	private CommandLine cmdLine, cmdFirstLine;
	
	public CommandLineOptions(String[] args) {
		this.options = new Options();
		this.firstOptions = new Options();
		this.initOptions();
		this.parser = new DefaultParser();
		this.parse(args);
	}

	private void parse(String[] args) {
		try {
			this.cmdFirstLine = this.parser.parse(this.firstOptions, args, true);
			if (this.cmdFirstLine.hasOption(HELP.getValue0())) {
		        final HelpFormatter formatter = new HelpFormatter();
		        formatter.printHelp(TSOPEN, options, true);
		        System.exit(0);
		    }
			this.cmdLine = this.parser.parse(this.options, args);
		} catch (ParseException e) {
			System.err.println("[!] " + e.getMessage());
			System.exit(1);
		}
	}

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
		
		this.firstOptions.addOption(help);
		
		this.options.addOption(file);
		this.options.addOption(platforms);
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

}
