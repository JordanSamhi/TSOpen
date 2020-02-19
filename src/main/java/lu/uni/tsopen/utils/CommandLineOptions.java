package lu.uni.tsopen.utils;

/*-
 * #%L
 * TSOpen - Open-source implementation of TriggerScope
 * 
 * Paper describing the approach : https://seclab.ccs.neu.edu/static/publications/sp2016triggerscope.pdf
 * 
 * %%
 * Copyright (C) 2019 Jordan Samhi
 * University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 *
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

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
	private static final Triplet<String, String, String> OUTPUT =
			new Triplet<String, String, String>("output", "o", "Output results in given file");
	private static final Triplet<String, String, String> QUIET =
			new Triplet<String, String, String>("quiet", "q", "Do not output results in console");
	private static final Triplet<String, String, String> CALLGRAPH =
			new Triplet<String, String, String>("callgraph", "c", "Define the call-graph algorithm to use (SPARK, CHA, RTA, VTA)");
	private static final Triplet<String, String, String> RAW =
			new Triplet<String, String, String>("raw", "r", "write raw results in stdout");
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


		final Option output = Option.builder(OUTPUT.getValue1())
				.longOpt(OUTPUT.getValue0())
				.desc(OUTPUT.getValue2())
				.hasArg(true)
				.argName(OUTPUT.getValue0())
				.build();

		final Option quiet = Option.builder(QUIET.getValue1())
				.longOpt(QUIET.getValue0())
				.desc(QUIET.getValue2())
				.argName(QUIET.getValue0())
				.build();

		final Option raw = Option.builder(RAW.getValue1())
				.longOpt(RAW.getValue0())
				.desc(RAW.getValue2())
				.argName(RAW.getValue0())
				.build();

		final Option callgraph = Option.builder(CALLGRAPH.getValue1())
				.longOpt(CALLGRAPH.getValue0())
				.desc(CALLGRAPH.getValue2())
				.argName(CALLGRAPH.getValue0())
				.hasArg(true)
				.build();
		timeout.setOptionalArg(true);

		this.firstOptions.addOption(help);

		this.options.addOption(file);
		this.options.addOption(platforms);
		this.options.addOption(exceptions);
		this.options.addOption(timeout);
		this.options.addOption(output);
		this.options.addOption(quiet);
		this.options.addOption(callgraph);
		this.options.addOption(raw);

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

	public boolean hasOutput() {
		return this.cmdLine.hasOption(OUTPUT.getValue1());
	}

	public String getOutput() {
		return this.cmdLine.getOptionValue(OUTPUT.getValue0());
	}

	public boolean hasQuiet() {
		return this.cmdLine.hasOption(QUIET.getValue1());
	}

	public boolean hasRaw() {
		return this.cmdLine.hasOption(RAW.getValue1());
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

	public String getCallGraph() {
		String cg = this.cmdLine.getOptionValue(CALLGRAPH.getValue0());
		if(cg != null) {
			if(cg.equals("SPARK") || cg.equals("CHA") || cg.equals("RTA") || cg.equals("VTA")) {
				return cg;
			}
        }
		return null;
	}
}
