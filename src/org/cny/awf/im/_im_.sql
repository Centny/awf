-- ----------------------------
--  Table structure for _IM_M_
-- ----------------------------
CREATE TABLE _IM_M_ (
	 I TEXT(256,0) PRIMARY KEY NOT NULL,
	 IDX INTEGER(64,0) NOT NULL,
	 S TEXT(256,0) NOT NULL,
	 R TEXT(1024,0) NOT NULL,
	 D TEXT(256,0),
	 T INTEGER(8,0) NOT NULL,
	 C TEXT(2048,0) NOT NULL,
	 A TEXT(256,0),
	 TIME INTEGER(64,0) NOT NULL,
	 STATUS INTEGER(8,0) NOT NULL
);

-- ----------------------------
--  Table structure for _HC_ENV_
-- ----------------------------
CREATE TABLE _IM_ENV_ (
	 NAME TEXT(256,0) NOT NULL,
	 VAL TEXT(256,0) NOT NULL,
	 TYPE TEXT(256,0) NOT NULL
);