// fluctuate - "Drift" b1-vsc-soft, d2-spatia-soft

(

~fvsc = [274.72, 1754.7844527117, 1475.8823642745, 742.65807811177, 523.69432084922, 439.24017880714, 364.10586233129];
~fspatia = [137.535309, 141.977798, 216.161057, 243.542664, 258.400299, 261.461395];
Ora.addSynths;

)

(

{
	OraPlay(Ora(~fvsc.scramble).fluctuate(0.005), numChannels:8)
		.clusterGrad(0.0, 0.3, 1.5, 6, \drsoft, 0.02, 3.0, 0.6);

	0.01.wait;
	~fvsc.do { |f, i|
		NF(("sineA" ++ i).asSymbol, {|amp = 0.02|
			var drift = LFNoise2.ar(0.003).range(f * 0.999, f * 1.001);
			var ampMod = LFNoise2.ar(rrand(0.02, 0.08)).range(0.1, 1.0);
			SinOsc.ar(drift) * amp * ampMod * XLine.ar(0.0001, 1.0, rrand(5, 12));
		}).play(i.mod(8));
	};

	15.wait;
	~fvsc.size.do { |i| NF(("sineA" ++ i).asSymbol).clear(rrand(8, 16)) };
}.fork;

)

(

OraPlay(Ora(~fspatia).fluctuate(0.003), numChannels:8)
	.clusterGrad(0.0, 0.3, 1.5, 6, \drsoft, 0.02, 3.5, 0.5);

)

(

OraPlay(Ora(~fvsc.scramble).fluctuate(0.008), numChannels:8)
	.clusterGrad(0.0, 0.25, 1.8, 6, \drsoft, 0.02, 2.5, 0.5);

)