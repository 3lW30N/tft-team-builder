-- 03-recipes.sql

-- 1. Les 10 composants
insert into item (id, name) values
                                ('BFSword',            'B.F. Sword'),
                                ('ChainVest',          'Chain Vest'),
                                ('FryingPan',          'Frying Pan'),
                                ('GiantsBelt',         'Giant''s Belt'),
                                ('NeedlesslyLargeRod', 'Needlessly Large Rod'),
                                ('NegatronCloak',      'Negatron Cloak'),
                                ('RecurveBow',         'Recurve Bow'),
                                ('SparringGloves',     'Sparring Gloves'),
                                ('Spatula',            'Spatula'),
                                ('TearOfTheGoddess',   'Tear of the Goddess')
on conflict do nothing;

-- 2. Les items combinés : (id, nom, composant A, composant B)
insert into item (id, name, component1_id, component2_id)
select id, name, least(c1, c2), greatest(c1, c2)   -- ordre exigé par chk_item_component_order
from (values
          -- B.F. Sword
          ('Deathblade',         'Deathblade',          'BFSword', 'BFSword'),
          ('EdgeOfNight',        'Edge of Night',       'BFSword', 'ChainVest'),
          ('HunterEmblem',       'Hunter Emblem',       'BFSword', 'FryingPan'),
          ('SteraksGage',        'Sterak''s Gage',      'BFSword', 'GiantsBelt'),
          ('HextechGunblade',    'Hextech Gunblade',    'BFSword', 'NeedlesslyLargeRod'),
          ('Bloodthirster',      'Bloodthirster',       'BFSword', 'NegatronCloak'),
          ('GiantSlayer',        'Giant Slayer',        'BFSword', 'RecurveBow'),
          ('InfinityEdge',       'Infinity Edge',       'BFSword', 'SparringGloves'),
          ('FaeEmblem',          'Fae Emblem',          'BFSword', 'Spatula'),
          ('SpearOfShojin',      'Spear of Shojin',     'BFSword', 'TearOfTheGoddess'),
          -- Chain Vest
          ('BrambleVest',        'Bramble Vest',        'ChainVest', 'ChainVest'),
          ('VanguardEmblem',     'Vanguard Emblem',     'ChainVest', 'FryingPan'),
          ('SunfireCape',        'Sunfire Cape',        'ChainVest', 'GiantsBelt'),
          ('Crownguard',         'Crownguard',          'ChainVest', 'NeedlesslyLargeRod'),
          ('GargoyleStoneplate', 'Gargoyle Stoneplate', 'ChainVest', 'NegatronCloak'),
          ('TitansResolve',      'Titan''s Resolve',    'ChainVest', 'RecurveBow'),
          ('SteadfastHeart',     'Steadfast Heart',     'ChainVest', 'SparringGloves'),
          ('ElderwoodEmblem',    'Elderwood Emblem',    'ChainVest', 'Spatula'),
          ('ProtectorsVow',      'Protector''s Vow',    'ChainVest', 'TearOfTheGoddess'),
          -- Frying Pan
          ('TacticiansShield',   'Tactician''s Shield', 'FryingPan', 'FryingPan'),
          ('BrawlerEmblem',      'Brawler Emblem',      'FryingPan', 'GiantsBelt'),
          ('SpellweaverEmblem',  'Spellweaver Emblem',  'FryingPan', 'NeedlesslyLargeRod'),
          ('RavagerEmblem',      'Ravager Emblem',      'FryingPan', 'NegatronCloak'),
          ('RapidfireEmblem',    'Rapidfire Emblem',    'FryingPan', 'RecurveBow'),
          ('ExecutionerEmblem',  'Executioner Emblem',  'FryingPan', 'SparringGloves'),
          ('TacticiansCape',     'Tactician''s Cape',   'FryingPan', 'Spatula'),
          ('InvokerEmblem',      'Invoker Emblem',      'FryingPan', 'TearOfTheGoddess'),
          -- Giant's Belt
          ('WarmogsArmor',       'Warmog''s Armor',     'GiantsBelt', 'GiantsBelt'),
          ('Morellonomicon',     'Morellonomicon',      'GiantsBelt', 'NeedlesslyLargeRod'),
          ('Evenshroud',         'Evenshroud',          'GiantsBelt', 'NegatronCloak'),
          ('NashorsTooth',       'Nashor''s Tooth',     'GiantsBelt', 'RecurveBow'),
          ('StrikersFlail',      'Striker''s Flail',    'GiantsBelt', 'SparringGloves'),
          ('BlackthornEmblem',   'Blackthorn Emblem',   'GiantsBelt', 'Spatula'),
          ('SpiritVisage',       'Spirit Visage',       'GiantsBelt', 'TearOfTheGoddess'),
          -- Needlessly Large Rod
          ('RabadonsDeathcap',   'Rabadon''s Deathcap', 'NeedlesslyLargeRod', 'NeedlesslyLargeRod'),
          ('IonicSpark',         'Ionic Spark',         'NeedlesslyLargeRod', 'NegatronCloak'),
          ('GuinsoosRageblade',  'Guinsoo''s Rageblade','NeedlesslyLargeRod', 'RecurveBow'),
          ('JeweledGauntlet',    'Jeweled Gauntlet',    'NeedlesslyLargeRod', 'SparringGloves'),
          ('BlossomEmblem',      'Blossom Emblem',      'NeedlesslyLargeRod', 'Spatula'),
          ('ArchangelsStaff',    'Archangel''s Staff',  'NeedlesslyLargeRod', 'TearOfTheGoddess'),
          -- Negatron Cloak
          ('DragonsClaw',        'Dragon''s Claw',      'NegatronCloak', 'NegatronCloak'),
          ('KrakensFury',        'Kraken''s Fury',      'NegatronCloak', 'RecurveBow'),
          ('Quicksilver',        'Quicksilver',         'NegatronCloak', 'SparringGloves'),
          ('SprykinEmblem',      'Sprykin Emblem',      'NegatronCloak', 'Spatula'),
          ('AdaptiveHelm',       'Adaptive Helm',       'NegatronCloak', 'TearOfTheGoddess'),
          -- Recurve Bow
          ('RedBuff',            'Red Buff',            'RecurveBow', 'RecurveBow'),
          ('LastWhisper',        'Last Whisper',        'RecurveBow', 'SparringGloves'),
          ('InfernoEmblem',      'Inferno Emblem',      'RecurveBow', 'Spatula'),
          ('VoidStaff',          'Void Staff',          'RecurveBow', 'TearOfTheGoddess'),
          -- Sparring Gloves
          ('ThiefsGloves',       'Thief''s Gloves',     'SparringGloves', 'SparringGloves'),
          ('PrimalEmblem',       'Primal Emblem',       'SparringGloves', 'Spatula'),
          ('HandOfJustice',      'Hand of Justice',     'SparringGloves', 'TearOfTheGoddess'),
          -- Spatula
          ('TacticiansCrown',    'Tactician''s Crown',  'Spatula', 'Spatula'),
          ('LunarEmblem',        'Lunar Emblem',        'Spatula', 'TearOfTheGoddess'),
          -- Tear of the Goddess
          ('BlueBuff',           'Blue Buff',           'TearOfTheGoddess', 'TearOfTheGoddess')
     ) as r(id, name, c1, c2)
on conflict (id) do update
    set component1_id = excluded.component1_id,
        component2_id = excluded.component2_id;