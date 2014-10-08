package main

import (
	"net/http"
	"org.cny.amf.ts/tdl"
)

func main() {
	tdl.Register()
	http.ListenAndServe(":8000", nil)
}
