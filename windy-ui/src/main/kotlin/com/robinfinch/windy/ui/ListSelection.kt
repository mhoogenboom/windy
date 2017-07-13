package com.robinfinch.windy.ui

interface ListSelection<T> {

}

class Selected<T>(val item: T) : ListSelection<T> {

}


class Clear<T> : ListSelection<T> {

}