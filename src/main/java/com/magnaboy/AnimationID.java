package com.magnaboy;

import lombok.Getter;

@Getter
public enum AnimationID {
	Flex(8917),
	Think(857),
	Yawn(2111),
	SlapHead(4275),
	HumanIdle(808),
	HumanWalk(819),
	Woodcutting(2117),
	HalfLayingDown(1147),
	Sitting(4114),
	ChurchSitting(3281),
	CurledUp(4712),

	// Actions
	Grabbing(551),
	Eat(829),
	RangeCook(896),
	Alching(713),
	FireCook(897),
	FurnaceSmelt(899),
	HerbloreMix(363),
	Fletching(1248),
	AnvilBang(898),
	Crying(860),
	Mining(1728),
	BuryOrPickingUp(827),
	LayingDown(838),
	HumanLook(2713),
	WateringCanPour(2293),
	Fishing(622),

	ChildStarJump(218),
	ChildPlay1(6484),
	ChildPlay2(6485),
	ChildWalk(189),
	ChildIdle(195),

	SuzieIdle(10060),
	LectorIdle(5875),
	LectorWalk(5876),

	FallenManDead(6280),
	FallenManIdle(6282),

	// Non-human
	FireIdle(475),
	CatLunge(319),
	CatSit(2134),
	CatSleep(2159),
	RatIdle(2704),
	RatBanging(2706),
	BeeIdle(0),
	PuffinIdle(5873),
	PuffinWalk(5872),

	RiftGuardianIdle(7307),
	RiftGuardianWalk(7306),
	RiftGuardianSit(9397),

	CowIdle(180),
	CowWalk(229),

	TanglerootIdle(7312),
	TanglerootWalk(7313),

	TrollIdle(286),
	TrollWalk(283),

	DwarfLean(6206),
	DwarfWalk(98),
	DwarfMining(99),
	DwarfMining2(4021),
	DwarfIdle(101),
	DwarfSmith(4021),
	DwarfSit(2337),
	DwarfHandsBehindBack(2151),

	ChickenIdle(5386),
	ChickenWalk(5385),

	GoblinPull(3387),

	PigeonIdle(4133),

	MagicBoxIdle(5221),
	StandingWithBook(1350),
	WalkingWithBook(10170),

	KittenSit(2694),
	KittenWalk(314),
	KittenLunge(315),
	KittenDip(316),
	KittenIdle(317),
	KittenSleep(2159),

	SheepDogIdle(2268),

	// ???
	FrontalGrab(897),
	ChestRub(190);

	private final Integer id;

	AnimationID(Integer id) {
		this.id = id;
	}

}
