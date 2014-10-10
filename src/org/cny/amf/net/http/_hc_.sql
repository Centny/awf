-- ----------------------------
--  Table structure for _HC_R_
-- ----------------------------
CREATE TABLE _HC_R_ (
	 TID INTEGER PRIMARY KEY NOT NULL,
	 U TEXT(256,0) NOT NULL,
	 M TEXT(256,0) NOT NULL,
	 ARG TEXT(1024,0),
	 LMT INTEGER(64,0),
	 ETAG TEXT(256,0),
	 TYPE TEXT(255,0),
	 LEN INTEGER(32,0) NOT NULL,
	 PATH TEXT(512,0) NOT NULL,
	 TIME INTEGER(64,0) NOT NULL
);

-- ----------------------------
--  Table structure for _HC_ENV_
-- ----------------------------
CREATE TABLE _HC_ENV_ (
	 NAME TEXT(256,0) NOT NULL,
	 VAL TEXT(256,0) NOT NULL,
	 TYPE TEXT(256,0) NOT NULL
);
