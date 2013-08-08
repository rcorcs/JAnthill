package dcc.ufmg.anthill.mapred;

import java.io.IOException;

import java.util.AbstractMap.SimpleEntry;

import dcc.ufmg.anthill.Filter;
import dcc.ufmg.anthill.stream.StreamNotWritable;

public abstract class Mapper< SimpleEntry<Comparable, InputType>, SimpleEntry<Comparable, OutputType> >
extends Filter< SimpleEntry<Comparable, InputType>, SimpleEntry<Comparable, OutputType> > {
}
