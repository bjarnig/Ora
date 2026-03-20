// retrograde, inversion, retrogradeInversion - "Drift" a2-nsane, b1-vsc-soft

(
~fnsane = [178.38052971582, 255.19458012749, 884.64258085768, 1748.7291446291, 9043.8864701014, 4473.3283429036, 1257.7392833936] * 0.5;
~fvsc = [274.72, 1754.7844527117, 1475.8823642745, 742.65807811177, 523.69432084922, 439.24017880714, 364.10586233129];
Ora.addSynths;
)

(
OraPlay(Ora(~fnsane).retrogradeInversion, numChannels:8)
	.clusterGrad(0.04, 0.08, 1.2, 4, \drpm, 0.1, 0.75, 1.0);
)

(
OraPlay(
	Ora(~fnsane * 0.5).retrogradeInversion.intervalFlow(0.1),
	numChannels:8
).clusterGrad(0.08, 0.2, 1.4, 1, \drpm, 0.1, 0.75, 1.0);
)

(
OraPlay(Ora(~fvsc).inversion, numChannels:2)
	.clusterGrad(0.02, 0.15, 1.5, 4, \drsoft, 0.04, 1.5);
)

(
OraPlay(Ora(~fvsc).retrograde.detune(4), numChannels:8)
	.clusterGrad(0.02, 0.2, 1.2, 4, \drsoft, 0.04, 1.5, 0.5);
)

(
{
	OraPlay(Ora(~fnsane), numChannels:8)
		.clusterGrad(0.04, 0.12, 1.5, 4, \drpm, 0.2, 1.5, 1.0);
	6.wait;
	OraPlay(Ora(~fnsane).retrograde, numChannels:8)
		.clusterGrad(0.04, 0.12, 1.5, 4, \drpm, 0.2, 1.5, 1.0);
	6.wait;
	OraPlay(Ora(~fnsane).inversion, numChannels:8)
		.clusterGrad(0.04, 0.12, 1.5, 4, \drpm, 0.2, 1.5, 1.0);
	6.wait;
	OraPlay(Ora(~fnsane).retrogradeInversion, numChannels:8)
		.clusterGrad(0.04, 0.12, 1.5, 4, \drpm, 0.2, 1.5, 1.0);
}.fork
)
