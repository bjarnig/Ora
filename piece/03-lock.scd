// geomeanLock - "Drift" a2-nsane

(

~fnsane = [178.38052971582, 255.19458012749, 884.64258085768, 1748.7291446291, 9043.8864701014, 4473.3283429036, 1257.7392833936] * 0.5;
~fvsc = [274.72, 1754.7844527117, 1475.8823642745, 742.65807811177, 523.69432084922, 439.24017880714, 364.10586233129];
Ora.addSynths;

)

(

{
	OraPlay(Ora(~fnsane.reverse), numChannels:8)
		.clusterGrad(0.02, 0.3, 1.2, 3, \drpm, 0.3, 0.75, 1.0);

	5.wait;

	OraPlay(Ora(~fnsane).geomeanLock, numChannels:8)
		.clusterGrad(0.1, 0.3, 1.2, 4, \drpm, 0.3, 0.75, 1.2);

	9.wait;

	OraPlay(Ora(~fnsane).intervalFlow, numChannels:8)
		.clusterGrad(0.02, 0.1, 1.2, 4, \drpm, 0.1, 0.75, 1);

}.fork

)

(

OraPlay(Ora(~fvsc.reverse).geomeanLock, numChannels:8)
	.clusterGrad(0.02, 0.2, 1.2, 4, \drsoft, 0.04, 1.5, 0.5);

)