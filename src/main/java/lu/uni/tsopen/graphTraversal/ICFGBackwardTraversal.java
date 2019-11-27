package lu.uni.tsopen.graphTraversal;

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

import java.util.Collection;
import java.util.List;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;

/**
 * Implementation of the backward ICFG traversal
 * @author Jordan Samhi
 *
 */
public abstract class ICFGBackwardTraversal extends ICFGTraversal {

	public ICFGBackwardTraversal(InfoflowCFG icfg, String nameOfAnalysis, SootMethod mainMethod) {
		super(icfg, nameOfAnalysis, mainMethod);
	}

	@Override
	public List<Unit> getNeighbors(Unit u) {
		return this.icfg.getPredsOf(u);
	}

	@Override
	public Collection<Unit> getExtremities(SootMethod m) {
		return this.icfg.getEndPointsOf(m);
	}

}
