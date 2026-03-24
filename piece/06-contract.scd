// contract, expand, detune - "Drift" b1-vsc-soft, d2-spatia-soft, e2-dynmc-soft

(

~fvsc = [274.72, 1754.7844527117, 1475.8823642745, 742.65807811177, 523.69432084922, 439.24017880714, 364.10586233129];
~fspatia = [137.535309, 141.977798, 216.161057, 243.542664, 258.400299, 261.461395];
Ora.addSynths;

)

(

OraPlay(Ora(~fvsc).contract(amount: 0.6).detune(2), numChannels:8)
	.clusterGrad(0.02, 0.2, 1.2, 4, \drsoft, 0.04, 1.8, 0.5);

)

(

NF(\silkExpand, {|harms = 6, amp = 0.004|
	var freqs = Ora(~fvsc).expand(amount: 1.2).asArray;
	var mod = SinOsc.ar(
		LFNoise2.kr(0.03).range(0.05, 0.3),
		0,
		LFNoise1.kr(0.02).range(0.5, 2)
	);
	var sig = Splay.ar(Blip.ar(freqs + mod, harms));
	sig = LPF.ar(sig, 6000);
	sig * amp * XLine.ar(0.0001, 1.0, 14);
}).play(8);

)

NF(\silkExpand).clear(10);

(

OraPlay(Ora(~fspatia).retrograde.contract(amount: 0.8), numChannels:8)
	.clusterGrad(0.02, 0.2, 1.2, 4, \drsoft, 0.04, 2.0, 0.3);

)

(

{
	OraPlay(Ora(~fvsc).expand(amount: 1.2), numChannels:8)
		.clusterGrad(0.0, 0.25, 1.5, 5, \drsoft, 0.03, 2.0, 0.6);
	10.wait;
	OraPlay(Ora(~fvsc).detune(2), numChannels:8)
		.clusterGrad(0.02, 0.2, 1.2, 4, \drsoft, 0.04, 1.5, 0.5);
	10.wait;
	OraPlay(Ora(~fvsc).contract(amount: 0.6).detune(4), numChannels:8)
		.clusterGrad(0.02, 0.15, 1.2, 4, \drsoft, 0.05, 1.5, 0.4);
}.fork

)
