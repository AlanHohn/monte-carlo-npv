monte-carlo-npv [![Build Status](https://travis-ci.org/AlanHohn/monte-carlo-npv.png)](https://travis-ci.org/AlanHohn/monte-carlo-npv)
===============

This small application provides a Net Present Value calculator that allows for
uncertainty in the cash flows and discount rate by using a Monte Carlo
simulation. Each independent iteration of the Monte Carlo selects one set of
values from the cash flow and discount rate distributions.

The primary purpose of this application is to illustrate the Fork/Join
framework introduced with Java SE 7. As the Monte Carlo iterations are
independent, it is possible to divide the work between multiple tasks
and then collect the statistics.
 
