-- ----------------------------
--  Table structure for _IM_M_
-- ----------------------------
CREATE TABLE _IM_M_ (
	 I TEXT(256,0) PRIMARY KEY NOT NULL,
	 S TEXT(256,0) NOT NULL,
	 R TEXT(1024,0) NOT NULL,
	 D TEXT(256,0),
	 T INTEGER(8,0) NOT NULL,
	 C TEXT(2048,0) NOT NULL,
	 TIME INTEGER(64,0) NOT NULL
);
