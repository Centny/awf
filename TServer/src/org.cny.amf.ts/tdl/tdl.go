package tdl

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"strconv"
)

func Register() {
	fmt.Println("registering all downloa handle ...")
	http.HandleFunc("/dl", hdl_n)
	http.HandleFunc("/dl_p", hdl_p)
	http.HandleFunc("/g_args", hdl_gs)
	http.HandleFunc("/g_argsc", hdl_gsc)
	http.HandleFunc("/p_args", hdl_ps)
	http.HandleFunc("/h_args", hdl_hs)
	http.HandleFunc("/t_args", hdl_ts)
	http.HandleFunc("/rec_f", rec_f)
	http.HandleFunc("/res_j", res_j)
}
func hdl_ts(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/plain;")
	w.Header().Set("Abb", "中文数据")
	w.Write([]byte("kkkkkk"))
}

var tccc = 0

func hdl_gsc(w http.ResponseWriter, r *http.Request) {
	if len(r.Header.Get("If-Modified-Since")) > 0 ||
		len(r.Header.Get("If-None-Match")) > 0 {
		w.WriteHeader(304)
		// w.Write([]byte("OK"))
	} else {
		w.Header().Set("Last-Modified", "Wed, 04 Feb 2015 15:57:18 GMT")
		w.Header().Set("ETag", "abbccc")
		w.Write([]byte("OK"))
	}
	// if tccc < 1 {
	// 	w.Header().Set("Last-Modified", "Wed, 04 Feb 2015 15:57:18 GMT")
	// 	w.Header().Set("ETag", "abbccc")
	// 	w.Write([]byte("OK"))
	// 	tccc++
	// } else {
	// 	w.WriteHeader(304)
	// 	w.Write([]byte("OK"))
	// 	tccc++
	// }
}
func hdl_gs(w http.ResponseWriter, r *http.Request) {
	var v string
	v = r.FormValue("a")
	if len(v) > 1 {
		w.Write([]byte("ERR"))
		return
	}
	v = r.FormValue("b")
	if len(v) < 2 {
		w.Write([]byte("ERR"))
		return
	}
	v = r.FormValue("c")
	if v != "这是中文" {
		w.Write([]byte("ERR"))
		return
	}
	w.Header().Set("Last-Modified", "Wed, 04 Feb 2015 15:57:18 GMT")
	w.Write([]byte("OK"))
}
func hdl_ps(w http.ResponseWriter, r *http.Request) {
	var v string
	v = r.PostFormValue("a")
	if len(v) > 1 {
		w.Write([]byte("ERR"))
		return
	}
	v = r.PostFormValue("b")
	if len(v) < 2 {
		w.Write([]byte("ERR"))
		return
	}
	v = r.PostFormValue("c")
	if v != "这是中文" {
		w.Write([]byte("ERR"))
		return
	}
	w.Write([]byte("OK"))
}
func toUtf8(iso8859_1_buf []byte) string {
	buf := make([]rune, len(iso8859_1_buf))
	for i, b := range iso8859_1_buf {
		buf[i] = rune(b)
	}
	return string(buf)
}
func hdl_hs(w http.ResponseWriter, r *http.Request) {
	for k, v := range r.Header {
		fmt.Println(k, v)
	}
	var v string
	v = r.Header.Get("a")
	if len(v) > 1 {
		w.Write([]byte("ERR"))
		return
	}
	v = r.Header.Get("b")
	if len(v) < 2 {
		w.Write([]byte("ERR"))
		return
	}
	v = r.Header.Get("c")
	if v != "这是中文" {
		fmt.Println(v)
		w.Write([]byte("ERR"))
		return
	}
	w.Header().Set("Last-Modified", "Wed, 04 Feb 2015 15:57:18 GMT")
	w.Write([]byte("OK"))
}
func hdl_n(w http.ResponseWriter, r *http.Request) {
	sw := r.FormValue("sw")
	swi, err := strconv.Atoi(sw)
	if err != nil {
		w.Write([]byte(err.Error()))
	} else {
		tran_f(w, r, swi)
	}
}
func hdl_p(w http.ResponseWriter, r *http.Request) {
	sw := r.PostFormValue("sw")
	for k, v := range r.PostForm {
		fmt.Println(k, v)
	}
	swi, err := strconv.Atoi(sw)
	if err != nil {
		w.Write([]byte(err.Error()))
	} else {
		tran_f(w, r, swi)
	}
}
func tran_f(w http.ResponseWriter, r *http.Request, sw int) {
	fpath := "data/www.pdf"
	f, err := os.Open(fpath)
	if err != nil {
		w.Write([]byte("error:" + err.Error()))
		return
	}
	fi, err := f.Stat()
	if err != nil {
		w.Write([]byte("error:" + err.Error()))
		return
	}
	fmt.Println(fmt.Sprintf("sw:%d", sw))
	switch sw {
	case 1:
		w.Header().Set("Content-Type", "text/plain; charset=UTF-8")
		w.Header().Set("Content-Disposition", "attachment;filename=测试.pdf")
		w.Header().Set("Content-Length", fmt.Sprintf("%d", fi.Size()))
		w.Header().Set("ABC", "这是中文")
		break
	case 2:
		w.Header().Set("Content-Type", "text/plain;")
		w.Header().Set("Content-Disposition", "attachment;filename=测试.pdf")
		w.Header().Set("Content-Length", fmt.Sprintf("%d", fi.Size()))
		w.Header().Set("ABC", "这是中文")
		break
	case 3:
		// w.Header().Set("Content-Type", "text/plain;")
		w.Header().Set("Content-Disposition", "attachment;filename=测试.pdf")
		w.Header().Set("Content-Length", fmt.Sprintf("%d", fi.Size()))
		w.Header().Set("ABC", "这是中文")
		break
	case 4:
		w.Header().Set("Content-Type", "text/plain; charset=UTF-8")
		w.Header().Set("Content-Disposition", "attachment;filename=测试.pdf")
		// w.Header().Set("Content-Length", fmt.Sprintf("%d", fi.Size()))
		w.Header().Set("ABC", "这是中文")
		break
	case 5:
		w.Header().Set("Content-Type", "text/plain; charset=UTF-8")
		// w.Header().Set("Content-Disposition", "attachment;filename=测试.pdf")
		w.Header().Set("Content-Length", fmt.Sprintf("%d", fi.Size()))
		w.Header().Set("ABC", "这是中文")
		break
	}
	fmt.Println("transfter file ...")
	io.Copy(w, f)
}

func rec_f(w http.ResponseWriter, r *http.Request) {
	r.ParseForm()

	fmt.Println(r.PostForm, r.Form)
	// fmt.Println(r.FormFile("sw"))
	rf, fi, err := r.FormFile("file")
	if err != nil {
		w.Write([]byte("get error:" + err.Error()))
		return
	}
	fmt.Println(fi.Filename)
	fpath := "data/www.txt"
	tf, err := os.OpenFile(fpath, os.O_CREATE|os.O_RDWR, os.ModePerm)
	if err != nil {
		w.Write([]byte("open error:" + err.Error()))
		return
	}
	defer tf.Close()
	wl, err := io.Copy(tf, rf)
	if err != nil {
		w.Write([]byte("copy error:" + err.Error()))
		return
	}
	fmt.Println("copy len:", wl)
}

type Ab struct {
	I int
	S string
}

func res_j(w http.ResponseWriter, r *http.Request) {
	ab := Ab{I: 1, S: "sss"}
	by, _ := json.Marshal(&ab)
	w.Write(by)
}
