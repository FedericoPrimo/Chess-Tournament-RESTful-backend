// Extract match data for players who played as Black
db.tournaments.aggregate([
    { $unwind: "$RawMatches" },
    { 
        $project: { 
            _id: { 
                $function: { 
                    body: function() { return new ObjectId(); }, 
                    args: [], lang: "js" 
                } 
            }, 
            playerId: "$RawMatches.Black", 
            color: "Black", 
            opponentId: "$RawMatches.White", 
            moves: { $size: "$RawMatches.Moves" }, 
            winner: { 
                $cond: [ { $eq: ["$RawMatches.Winner", "$RawMatches.Black"] }, "win", 
                { $cond: [ { $eq: ["$RawMatches.Winner", "draw"] }, "draw", "loss" ] } ] 
            }, 
            opening: "$RawMatches.ECO", 
            ELO: "$RawMatches.BlackElo", 
            tournamentEdition: "$Edition", 
            tournamentCategory: "$Category" 
        } 
    }, 
    { $out: "black" }
]);
// Extract match data for players who played as White
db.tournaments.aggregate([
    { $unwind: "$RawMatches" },
    { 
        $project: { 
            _id: { 
                $function: { 
                    body: function() { return new ObjectId(); }, 
                    args: [], lang: "js" 
                } 
            }, 
            playerId: "$RawMatches.White", 
            color: "White", 
            opponentId: "$RawMatches.Black", 
            moves: { $size: "$RawMatches.Moves" }, 
            winner: { 
                $cond: [ { $eq: ["$RawMatches.Winner", "$RawMatches.White"] }, "win", 
                { $cond: [ { $eq: ["$RawMatches.Winner", "draw"] }, "draw", "loss" ] } ] 
            }, 
            opening: "$RawMatches.ECO", 
            ELO: "$RawMatches.WhiteElo", 
            tournamentEdition: "$Edition", 
            tournamentCategory: "$Category" 
        } 
    }, 
    { $out: "white" }
]);
// Combine data from "black" and "white" collections
db.white.aggregate([
    { $unionWith: "black" }, 
    { $out: "combined" }
]);
// Create the "user" collection by grouping matches by player
db.combined.aggregate([
    { 
        $group: { 
            _id: "$playerId", 
            ELO: { $last: "$ELO" }, 
            Matches: { 
                $push: {
                    Color: "$color", 
                    NumberOfMoves: "$moves", 
                    Outcome: "$winner", 
                    Opening: "$opening", 
                    OpponentId: "$opponentId", 
                    TournamentEdition: "$tournamentEdition", 
                    TournamentCategory: "$tournamentCategory"
                } 
            } 
        } 
    }, 
    { $out: "user" }
]);
// Update player data with name, surname, birthdate, type, and password
const users = [
    { _id: 'smyslov_vassily', BirthDate: '2000-07-28', Password:'$2a$10$6lfytGWURMcnUw.oS8ZZDOAd4iV5H3XHPwTiv8K6t4UuihIyI8dey' },
    { _id: 'kasparov_gary', BirthDate: '1945-03-11', Password:'$2a$10$VrCFrHzbTfBomry0jGhdquwrm0y5zNEkqbrHJ8EsFvWT0.C8dLMx6' },
    { _id: 'frattacci_jonathan', BirthDate: '1987-12-03', Password:'$2a$10$6OdY/TeQbAOXc01EeKlnxenQR4xPqRlNwb70fjAz1RWtPxrEqJQgm' },
    { _id: 'anand_viswanathan', BirthDate: '1990-06-24', Password:'$2a$10$868iA6jaMuJ622Zt.VTxDeYEKWtTvQq6hMIEEmy9GtAjUknyhJdsK' },
    { _id: 'lasker_emanuel', BirthDate: '1992-11-19', Password:'$2a$10$je7jeOBZlQ1UfbXKoHBGv.DoILsx7oTP5ruEy8mrfNNlKtSPCCPHe' },
    { _id: 'alekhine_alexander', BirthDate: '1976-03-17', Password:'$2a$10$Je/99MwCc8oE08xwdd45SuBvrgJiaK2IDx8zzlfSZ8N0K/F9J/M6e' },
    { _id: 'botvinnik_mikhail', BirthDate: '1994-10-15', Password:'$2a$10$5PGofe03.iyIV2ON9GMRs./P0WWYETu7otkblnFDdHbSKYKJMNEpe' },
    { _id: 'lucky_luciano', BirthDate: '1943-02-04', Password:'$2a$10$4bohAccDUO66mbxUCvEtg.IE03WGa9C9lOb.dxSfuhtlMO/XeJiua' },
    { _id: 'karpov_anatoly', BirthDate: '1987-11-26', Password:'$2a$10$On8yjqek1hwbUgVud5npZOav5IjrFfbhlrs57n8qFAyzxUAdxAbai' },
    { _id: 'kortschnoj_viktor', BirthDate: '1979-07-31', Password:'$2a$10$DrIXIpzoDMC2PM4Etzttw.M/SVBPP23HKqDBWe4qPr0NwkBgLU7Am' },
    { _id: 'carlsen_magnus', BirthDate: '1996-05-17', Password:'$2a$10$Jr.cxA/sqV8beTUV7sHede5YjTZhwhJoKsPDl8YOW7DK8c6YWsHjG' },
    { _id: 'gelfand_boris', BirthDate: '1950-10-03', Password:'$2a$10$SMg31AW6sqmwHsv5T81zqO4UAwN35pLW4i8ynCKJ2ZvYV7hemQCr2' },
    { _id: 'calzolari_federico', BirthDate: '2000-08-13', Password:'$2a$10$rB6vINUMVkAP213GDIBQRu5oojvTpW9fXaFwYhRjJVi/vCvqcc5Gy' },
    { _id: 'topalov_veselin', BirthDate: '1965-12-17', Password:'$2a$10$N6tkLd/3HcKgDCT77IW.y.eBOQAL9t8Y8cO2XDLxvIkJZhDlAVWD.' },
    { _id: 'segreto_mattia', BirthDate: '1946-02-13', Password:'$2a$10$1ppRHn1Q4BTEwkjVp7tVb..SLdYQVP/rTSPhf1EVWt5YW4mntWNKO'},
    { _id: 'steinitz_william', BirthDate: '1965-10-23', Password:'$2a$10$TGduMgY0qk.FFriAksQBiu0No/HWJi1qiAn7KF/INDLMa.w4bgoPq' },
    { _id: 'euwe_max', BirthDate: '1979-05-12', Password:'$2a$10$8HLLewIFIX/1xaMICnSl9.9T0qhxh.OZSP3YqvgnTKqksCZkvtBxq' }
];
users.forEach(user => {
    const parts = user._id.split('_');
    const surname = parts[0];
    const name = parts[1];
    db.user.updateOne(
        { _id: user._id }, 
        {
            $set: {
                BirthDate: user.BirthDate, 
                Surname: surname,          
                Name: name,                
                Type: '1',                 
                Password: user.Password        
            }
        }
    );
});
// Cleanup temporary collections
db.black.drop();
db.white.drop();
db.combined.drop();
//Insert Admins
db.user.insertMany([{
    "_id": "gojo_satoru",
    "BirthDate": "1989-12-7",
    "Name": "Satoru",
    "Password": "$2a$10$Nfg/icoEfFQifTZdTm.hr.NsuIpI1O/.E5ZAhaiPk67ExPL9B/xDq",
    "Surname": "Gojo",
    "Type": "0"
  },
  {
    "_id": "sung_jinwoo",
    "BirthDate": "2001-03-8",
    "Name": "Jinwoo",
    "Password": "$2a$10$oeYK1ftH3gyS6h4ZJLN1.exIur5LFgibE6R3WaBcVPix7t8z8gg86",
    "Surname": "Sung",
    "Type": "0"
  },
  {
    "_id": "mustang_roy",
    "BirthDate": "1975-03-25",
    "Name": "Roy",
    "Password": "$2a$10$UjKu3FTl4Q0GibTrf3wjx.SN/7xaBr3vfUVJYhgkgYfAHT/OCzqV6",
    "Surname": "Mustang",
    "Type": "0"
  },
  {
    "_id": "uchiha_itachi",
    "BirthDate": "1985-06-9",
    "Name": "Itachi",
    "Password": "$2a$10$i9PemlJcjBkxWD6YGif.eOp2BexgIZwHfa2DENrSJ.hQpYNFjFJG.",
    "Surname": "Uchiha",
    "Type": "0"
  }]);
//Insert Spectators
db.user.insertMany([{
	  "_id": "chang_megan",
	  "BirthDate": "1975-09-16",
	  "Name": "Megan",
	  "Password": "$2a$10$WJYSO9BpV6S2U4mjB/RRYO2hbT4Fo8MD0lFLFA9osYMsJ49s9Dvoi",
	  "Surname": "Chang",
	  "Type": "2"
	},
	{
	  "_id": "green_robert",
	  "BirthDate": "1955-12-27",
	  "Name": "Robert",
	  "Password": "$2a$10$T6rbcAZJJb6ystxtwmmsr.ABooMthh0hDP4g2Iy.AzhnPe3zqS10W",
	  "Surname": "Green",
	  "Type": "2"
	},
	{
	  "_id": "sullivan_william",
	  "BirthDate": "1963-02-26",
	  "Name": "William",
	  "Password": "$2a$10$Z0qMfqq0liIK9hidutU7kuvnyCy6WqKavrFQa6toIesZTRtdQmFhW",
	  "Surname": "Sullivan",
	  "Type": "2"
	},
	{
	  "_id": "turner_kristen",
	  "BirthDate": "1932-04-13",
	  "Name": "Kristen",
	  "Password": "$2a$10$qUeyWoRjL91gfkQvkdRA0eAlOPQqn5ac2h8AU5odqdizaXt1D0g1i",
	  "Surname": "Turner",
	  "Type": "2"
	},
	{
	  "_id": "silva_thomas",
	  "BirthDate": "1966-12-19",
	  "Name": "Thomas",
	  "Password": "$2a$10$h41or9TslRlrXRheptdIR.iqAqHZN4m2Uy03ClStuhfObVbt2vYEa",
	  "Surname": "Silva",
	  "Type": "2"
	},
	{
	  "_id": "wagner_rebecca",
	  "BirthDate": "1946-01-07",
	  "Name": "Rebecca",
	  "Password": "$2a$10$PCL1H1CsRjCT3aOANrYCweQUEZJaQ8blBFsDS8w29WWZ2h4MZ5vZ2",
	  "Surname": "Wagner",
	  "Type": "2"
	},
	{
	  "_id": "campos_juan",
	  "BirthDate": "1960-01-31",
	  "Name": "Juan",
	  "Password": "$2a$10$Br3ZEp/o.NBkqV8ieZEi5OPFEv3EHNauaJyrz7pXMiJ3XZ.VQlg9i",
	  "Surname": "Campos",
	  "Type": "2"
	},
	{
	  "_id": "king_christine",
	  "BirthDate": "1980-09-18",
	  "Name": "Christine",
	  "Password": "$2a$10$l9ABnvEkeOd33uWUpAAoDOCZp88E.tdBCGcr1qFATZ0N9QJQY.hMW",
	  "Surname": "King",
	  "Type": "2"
	},
	{
	  "_id": "mcgrath_renee",
	  "BirthDate": "1953-04-12",
	  "Name": "Renee",
	  "Password": "$2a$10$iO21ML7CndVFH.m1d9Pnde/7sfRbV.nBHWSE13kwe0SQLo4Umu.se",
	  "Surname": "Mcgrath",
	  "Type": "2"
	},
	{
	  "_id": "barrera_lisa",
	  "BirthDate": "1975-05-03",
	  "Name": "Lisa",
	  "Password": "$2a$10$LIkfTP/.8FDlxmzO49nDD./DEwxuDZyE9gfbkwjh3i3hOd1BnD/iy",
	  "Surname": "Barrera",
	  "Type": "2"
	},
	{
	  "_id": "blair_kyle",
	  "BirthDate": "1988-01-28",
	  "Name": "Kyle",
	  "Password": "$2a$10$TjxOnO.7B5cUkdSCLtu2p.Ll586okcPv1QvvEJzdHQ41aQ36iqSHe",
	  "Surname": "Blair",
	  "Type": "2"
	},
	{
	  "_id": "sutton_rachel",
	  "BirthDate": "1938-07-17",
	  "Name": "Rachel",
	  "Password": "$2a$10$jRWtrVdY6qJdfQQEwNdHEO.Tmt9JKQtLFoLFEzfH9w3JXYY8c1p0i",
	  "Surname": "Sutton",
	  "Type": "2"
	},
	{
	  "_id": "garcia_thomas",
	  "BirthDate": "1943-10-09",
	  "Name": "Thomas",
	  "Password": "$2a$10$HwBFe4qubmdNWAzt40HegOjWZ/yBpSsTzE6qZiMgBmdgiiY5h.i7m",
	  "Surname": "Garcia",
	  "Type": "2"
	},
	{
	  "_id": "carr_ryan",
	  "BirthDate": "1949-12-30",
	  "Name": "Ryan",
	  "Password": "$2a$10$5Jnc.xMtitN5xd5kcpJbRO2mnkAq0.l8yArTUjXQ50ota4A5NHRCa",
	  "Surname": "Carr",
	  "Type": "2"
	},
	{
	  "_id": "levy_robin",
	  "BirthDate": "1959-11-08",
	  "Name": "Robin",
	  "Password": "$2a$10$iyZhFMyLNff/Fy4au/E65erFq3Sero0RYaDBvDKesm09XzjV4ExgC",
	  "Surname": "Levy",
	  "Type": "2"
	},
	{
	  "_id": "grimes_thomas",
	  "BirthDate": "1936-03-26",
	  "Name": "Thomas",
	  "Password": "$2a$10$d8TlAwGW1CO/YSey4GAIIeQBAqOHAcKxU0xZzSWZeEXKm70b/5nJ6",
	  "Surname": "Grimes",
	  "Type": "2"
	},
	{
	  "_id": "trujillo_jorge",
	  "BirthDate": "2008-03-07",
	  "Name": "Jorge",
	  "Password": "$2a$10$zN0J2.20Wni/8w3MVx7YbuZSkgxVYzOoO.XCxJ/ZSU0xm46T530..",
	  "Surname": "Trujillo",
	  "Type": "2"
	},
	{
	  "_id": "smith_ana",
	  "BirthDate": "2002-10-13",
	  "Name": "Ana",
	  "Password": "$2a$10$2ouVbLe4gvY3w7nO1gfzeOw2Xj/GV3e659WVsnoegVPpMWPO1nwxq",
	  "Surname": "Smith",
	  "Type": "2"
	},
	{
	  "_id": "ross_jennifer",
	  "BirthDate": "2012-01-13",
	  "Name": "Jennifer",
	  "Password": "$2a$10$p9wudrvK1J9ZrJBCJrM3Je6OgLcRmEzs4f.atGpY6K8n4I5CxIRwq",
	  "Surname": "Ross",
	  "Type": "2"
	},
	{
	  "_id": "barnett_mallory",
	  "BirthDate": "1940-02-24",
	  "Name": "Mallory",
	  "Password": "$2a$10$rWgb2wl6W0pZwce0c//.ZewHNiAvNM2YK7VxTDbHFF5uYRjAfiuTK",
	  "Surname": "Barnett",
	  "Type": "2"
	},
	{
	  "_id": "snyder_aaron",
	  "BirthDate": "1933-09-17",
	  "Name": "Aaron",
	  "Password": "$2a$10$1Eb7nwUDTfSzrLOE/6Ke3Ob2KpaKd.wuy9SgY3Ct90dvXMf8KZV7i",
	  "Surname": "Snyder",
	  "Type": "2"
	},
	{
	  "_id": "sanchez_mikayla",
	  "BirthDate": "1947-01-06",
	  "Name": "Mikayla",
	  "Password": "$2a$10$ogYixBYkDIYBoqHBZ.YRi.kbf1F8GN994GP2eetpyjlcUMpgLSPvS",
	  "Surname": "Sanchez",
	  "Type": "2"
	},
	{
	  "_id": "harrell_mark",
	  "BirthDate": "1955-09-26",
	  "Name": "Mark",
	  "Password": "$2a$10$JONWIGOK8hLU1q2HfKNki.xiDXwf5U8psS/oxXIBG4jLniLR2kcDW",
	  "Surname": "Harrell",
	  "Type": "2"
	},
	{
	  "_id": "bradley_james",
	  "BirthDate": "2012-08-07",
	  "Name": "James",
	  "Password": "$2a$10$gziPjtSVILwQOsu1ppssreK5BoM8pTqdnrCTBT23WpmtQhAZ7PU52",
	  "Surname": "Bradley",
	  "Type": "2"
	},
	{
	  "_id": "ponce_john",
	  "BirthDate": "2008-06-18",
	  "Name": "John",
	  "Password": "$2a$10$JF4LQ2/hwfvkt.UEewDjVu3RyzqoNCbxYnFCqQn78MnXHrdXzwESy",
	  "Surname": "Ponce",
	  "Type": "2"
	},
	{
	  "_id": "west_linda",
	  "BirthDate": "1946-05-15",
	  "Name": "Linda",
	  "Password": "$2a$10$41xbSP.t2eY5QdMRcpHgRuwJU3XD8.YPRgEPFKN4Boe9ln2OOvypS",
	  "Surname": "West",
	  "Type": "2"
	},
	{
	  "_id": "flores_christopher",
	  "BirthDate": "1983-04-28",
	  "Name": "Christopher",
	  "Password": "$2a$10$HmfQ16lnwuejuvc6ECnB5.Qd7PspmEb3p3jejzPjC33nsYpEJZGC.",
	  "Surname": "Flores",
	  "Type": "2"
	},
	{
	  "_id": "cantu_william",
	  "BirthDate": "1942-07-15",
	  "Name": "William",
	  "Password": "$2a$10$zYKHodB54iLqujWq5x9xW.IVJuc4BEw/ZuqGcjPDIKvggzUnSx6by",
	  "Surname": "Cantu",
	  "Type": "2"
	},
	{
	  "_id": "arnold_daniel",
	  "BirthDate": "1980-06-14",
	  "Name": "Daniel",
	  "Password": "$2a$10$0Ay9yvhr50JFFHlP1Kqfzei0rdtRTAwGFLlTR0iU55W0wsvYJRutm",
	  "Surname": "Arnold",
	  "Type": "2"
	},
	{
	  "_id": "kelley_jasmine",
	  "BirthDate": "1945-11-14",
	  "Name": "Jasmine",
	  "Password": "$2a$10$Dqghro.T0TRPG2iZth02Xe0JhDqUP9QsJ8YyN.hQQfrOCM9njy5a.",
	  "Surname": "Kelley",
	  "Type": "2"
	},
	{
	  "_id": "fernandez_lisa",
	  "BirthDate": "1935-01-12",
	  "Name": "Lisa",
	  "Password": "$2a$10$eE3PjQhn3sfGae9i5W7iY.s0XIDpAcm/WmacgJvhLRjHIPX7p2e1i",
	  "Surname": "Fernandez",
	  "Type": "2"
	},
	{
	  "_id": "morrison_tamara",
	  "BirthDate": "2012-06-26",
	  "Name": "Tamara",
	  "Password": "$2a$10$5jnX6X6s3eCliRBjGLuQc.ssF/BR8aQ8fc9oHLnP5cQmUf2m17lBK",
	  "Surname": "Morrison",
	  "Type": "2"
	},
	{
	  "_id": "wallace_briana",
	  "BirthDate": "1933-01-08",
	  "Name": "Briana",
	  "Password": "$2a$10$7ELJE.zUjqIoPTsmT/PI3.JXbjWScgDSPKC5QcmkYg./upCnJL6qG",
	  "Surname": "Wallace",
	  "Type": "2"
	},
	{
	  "_id": "cruz_caitlyn",
	  "BirthDate": "1956-07-24",
	  "Name": "Caitlyn",
	  "Password": "$2a$10$WIFzHdne9Av0dpHzTLzmk.mwYU8aeoPfkB2kP7JwmL8IdPdq3pEq2",
	  "Surname": "Cruz",
	  "Type": "2"
	},
	{
	  "_id": "roberts_barbara",
	  "BirthDate": "1955-04-03",
	  "Name": "Barbara",
	  "Password": "$2a$10$zw06zhw4f3fJzgA4keCJAudAjhR8eA1SIYBRe94q1OAoJKGYjaFLi",
	  "Surname": "Roberts",
	  "Type": "2"
	},
	{
	  "_id": "lopez_jaime",
	  "BirthDate": "1946-12-09",
	  "Name": "Jaime",
	  "Password": "$2a$10$lUl/Mz.xhpm8JfT0u.Z2kuRGY3Drd81VJK32K.Ow5uEP/4qgaK7oy",
	  "Surname": "Lopez",
	  "Type": "2"
	},
	{
	  "_id": "douglas_chloe",
	  "BirthDate": "1995-04-20",
	  "Name": "Chloe",
	  "Password": "$2a$10$RcHvCi.ZA6PDgrHS2ozV/OvGhxUg50U7wjg5XA.0ROg/jE5BrljJG",
	  "Surname": "Douglas",
	  "Type": "2"
	},
	{
	  "_id": "davis_thomas",
	  "BirthDate": "1958-09-26",
	  "Name": "Thomas",
	  "Password": "$2a$10$fjK/BlSpTvUkVgC6W9CdJe014Ul2wetgnflrGzQMdzfkbBwqz.XGG",
	  "Surname": "Davis",
	  "Type": "2"
	},
	{
	  "_id": "mcdowell_katherine",
	  "BirthDate": "1938-05-23",
	  "Name": "Katherine",
	  "Password": "$2a$10$9ZPeajl5QQRPP0nL1/vZ3.ZNPA1foa77uixtnI2ZRUfn0W6P0JMtu",
	  "Surname": "Mcdowell",
	  "Type": "2"
	},
	{
	  "_id": "kirby_sandra",
	  "BirthDate": "1933-03-06",
	  "Name": "Sandra",
	  "Password": "$2a$10$eFXLicvPXxwijfDxAvkUGe2sgRWS8W7jr2srBWTnY3C7/nKrx6gfi",
	  "Surname": "Kirby",
	  "Type": "2"
	},
	{
	  "_id": "leblanc_rachael",
	  "BirthDate": "2004-02-24",
	  "Name": "Rachael",
	  "Password": "$2a$10$QxyG7GCACKLZz/OhZc7EfePTP0XD7eGEk7hNrVFNCvRZps/4hQtJ6",
	  "Surname": "Leblanc",
	  "Type": "2"
	},
	{
	  "_id": "myers_amber",
	  "BirthDate": "1988-01-02",
	  "Name": "Amber",
	  "Password": "$2a$10$S8bcPborpOUd1xYG3o1Atuuq7tOuJ2T5YPkf8KSPx7r8Euq1LUF0m",
	  "Surname": "Myers",
	  "Type": "2"
	},
	{
	  "_id": "hill_janet",
	  "BirthDate": "2014-07-17",
	  "Name": "Janet",
	  "Password": "$2a$10$CnOGPWQBZ8DIsU/hH1k/PugYlD9inflBkogH.bbzdPv//A.9tCi7a",
	  "Surname": "Hill",
	  "Type": "2"
	},
	{
	  "_id": "atkinson_lisa",
	  "BirthDate": "1943-11-22",
	  "Name": "Lisa",
	  "Password": "$2a$10$bfhzNc5Qu9kW1s2pg3/NzeUx4EuqNNmuoOlRlh6.WkNq6.IuNlo4q",
	  "Surname": "Atkinson",
	  "Type": "2"
	},
	{
	  "_id": "lawrence_patty",
	  "BirthDate": "1965-06-14",
	  "Name": "Patty",
	  "Password": "$2a$10$vieUmZfC6JJPN6tgVM/EGOiZuY8PVYvoR/bjL74QxDEHXDpzkuF5K",
	  "Surname": "Lawrence",
	  "Type": "2"
	},
	{
	  "_id": "riley_stephanie",
	  "BirthDate": "1939-08-09",
	  "Name": "Stephanie",
	  "Password": "$2a$10$NOjMyr8VTko84iq5sd/17.Zl7fRL0PAfrBbCqntjNjnox/itvO2Yy",
	  "Surname": "Riley",
	  "Type": "2"
	},
	{
	  "_id": "keller_shannon",
	  "BirthDate": "1960-02-18",
	  "Name": "Shannon",
	  "Password": "$2a$10$JhAQ7zJTtWa.r0yjS/otbeRE4tjXeJzsyUCBLIkc44A9Ej/Kd8eXq",
	  "Surname": "Keller",
	  "Type": "2"
	},
	{
	  "_id": "stark_wendy",
	  "BirthDate": "1939-11-15",
	  "Name": "Wendy",
	  "Password": "$2a$10$olmPniD1TtPX3kPCP/pWnOdutbFnv0/.qljygaH5aEIokfnUjYRYm",
	  "Surname": "Stark",
	  "Type": "2"
	},
	{
	  "_id": "miller_laura",
	  "BirthDate": "1971-01-19",
	  "Name": "Laura",
	  "Password": "$2a$10$Q83N6kjOPwl9246OmgoQlezRfWXkJpfeqhYypIkcBXN9bCF9ZoWLe",
	  "Surname": "Miller",
	  "Type": "2"
	},
	{
	  "_id": "tucker_chloe",
	  "BirthDate": "1977-10-02",
	  "Name": "Chloe",
	  "Password": "$2a$10$uqCGdwqBml2FJZq084DNzuClQjI/AywLgTfj8rTWHfCq7lcGMMbKi",
	  "Surname": "Tucker",
	  "Type": "2"
	}]);
 //Add Players' stats
 //number of matches
db.user.aggregate([
    { $match: { Type: '1' } },
    { $project: { 
        _id: 1, 
        NumberOfPlayedMatches: { $size: { $ifNull: ["$Matches", []] } } 
    } },
    { $set: { NumberOfPlayedMatches: { $ifNull: ["$NumberOfPlayedMatches", 0] } } }, 
    { $merge: { 
        into: "user", 
        on: "_id", 
        whenMatched: "merge", 
        whenNotMatched: "fail"
    } }
]);
//number of won matches
db.user.aggregate([
    { $match: { Type: '1' } },
    { $unwind: "$Matches"},
    { $match: { "Matches.Outcome": "win" } },
    { $group: { _id: "$_id", NumberOfVictories: { $sum: 1 } } }, 
    { $set: { NumberOfVictories: { $ifNull: ["$NumberOfVictories", 0] } } },
    { $merge: { 
        into: "user", 
        on: "_id", 
        whenMatched: "merge", 
        whenNotMatched: "fail"
    } }
]);
//number of lost matches
db.user.aggregate([
    { $match: { Type: '1' } },
    { $unwind: "$Matches"},
    { $match: { "Matches.Outcome": "loss" } },
    { $group: { _id: "$_id", NumberOfDefeats: { $sum: 1 } } },
    { $set: { NumberOfDefeats: { $ifNull: ["$NumberOfDefeats", 0] } } },
    { $merge: { 
        into: "user", 
        on: "_id", 
        whenMatched: "merge", 
        whenNotMatched: "fail"
    } }
]);
//number of draws
db.user.aggregate([
    { $match: { Type: '1' } },
    { $unwind: "$Matches"},
    { $match: { "Matches.Outcome": "draw" } },
    { $group: { _id: "$_id", NumberOfDraws: { $sum: 1 } } },
    { $set: { NumberOfDraws: { $ifNull: ["$NumberOfDraws", 0] } } },
    { $merge: { 
        into: "user", 
        on: "_id", 
        whenMatched: "merge", 
        whenNotMatched: "fail"
    } }
]);
//avg number of moves
db.user.aggregate([
    { $match: { Type: '1' } },
    { $unwind: "$Matches"},
    { $group: { _id: "$_id", avgMovesNumber: { $avg: { $ifNull: ["$Matches.NumberOfMoves", 0] } } } },
    { $set: { avgMovesNumber: { $ifNull: ["$avgMovesNumber", 0] } } },
    { $merge: { 
        into: "user", 
        on: "_id", 
        whenMatched: "merge", 
        whenNotMatched: "fail"
    } }
]);
// Update ELO for all players with Type=1
db.user.aggregate([
    { $match: { Type: "1" } },
    { $unwind: { path: "$Matches", preserveNullAndEmptyArrays: true } },
    { 
        $group: { 
            _id: "$_id",
            NumberOfVictories: { $sum: { $cond: [{ $eq: ["$Matches.Outcome", "win"] }, 1, 0] } },
            NumberOfDefeats: { $sum: { $cond: [{ $eq: ["$Matches.Outcome", "loss"] }, 1, 0] } },
            NumberOfDraws: { $sum: { $cond: [{ $eq: ["$Matches.Outcome", "draw"] }, 1, 0] } }
        }
    },
    { 
        $set: { 
            ELO: { 
                $max: [
                    0, 
                    { 
                        $subtract: [
                            { $add: [
                                { $multiply: ["$NumberOfVictories", 50] },
                                { $multiply: ["$NumberOfDraws", 10] }
                            ]},
                            { $multiply: ["$NumberOfDefeats", 50] }
                        ]
                    }
                ]
            }
        }
    },
    { 
        $merge: { 
            into: "user",
            on: "_id",
            whenMatched: "merge",
            whenNotMatched: "fail"
        } 
    }
]);
