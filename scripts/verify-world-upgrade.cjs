// REVIEW-UPGRADE-01: compare closed-world snapshots, not command input templates.
// Usage: node scripts/verify-world-upgrade.cjs original.json upgraded.json restarted.json
const fs = require('node:fs');
const assert = require('node:assert/strict');
const snapshots = process.argv.slice(2).map(file => JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, '')));
assert.equal(snapshots.length, 3, 'Supply original, upgraded and restarted snapshots');
const [original, upgraded, restarted] = snapshots;
let assertions = 0;
function equal(actual, expected, label) {
    assert.deepStrictEqual(actual, expected, label);
    ++assertions;
}
function component(value) {
    if (typeof value === 'string') {
        try { return component(JSON.parse(value)); } catch { return value; }
    }
    return value?.text ?? value;
}
const dogFields = [
    'id', 'UUID', 'Owner', 'level_normal', 'level_kami', 'dogGender', 'dogHunger',
    'mode', 'dogSize', 'dogIncapacitatedValue', 'Health', 'talents', 'accessories',
    'doggy_artifacts', 'doggy_groups', 'beds', 'bowls', 'entityKills', 'damageDealt',
    'distanceWalking', 'distanceSprinting', 'distanceRidden', 'distanceOnWater',
    'distanceInWater', 'distanceSneaking', 'doggytalents_dog_skin', 'friendlyFire',
    'regardTeamPlayers', 'hideDogArmor', 'crossOriginTp', 'lowHealthStrategy',
    'combatReturnStrategy', 'dogOnDuty', 'dogAutoMount', 'willObey', 'forceSit',
    'patrolTargetLock', 'Pos', 'NoAI', 'PersistenceRequired', 'Invulnerable'
];
function attributes(dog) {
    return dog.attributes.map(a => ({ ...a, id: a.id.replace(/^minecraft:(generic|horse|player|zombie)\./, 'minecraft:') }))
        .sort((a, b) => a.id.localeCompare(b.id));
}
function data(snapshot, file, field) {
    const entry = snapshot.savedData[file];
    assert.ok(entry, `Missing saved index ${file}`);
    return entry.data[field];
}
for (const [stage, target] of [['upgrade', upgraded], ['restart', restarted]]) {
    equal(target.dogs.length, original.dogs.length, `${stage}: dog count`);
    for (const source of original.dogs) {
        const dog = target.dogs.find(d => JSON.stringify(d.UUID) === JSON.stringify(source.UUID));
        assert.ok(dog, `${stage}: dog UUID missing`);
        for (const key of dogFields) equal(dog[key], source[key], `${stage}: dog ${source.UUID[0]} ${key}`);
        equal(component(dog.CustomName), component(source.CustomName), `${stage}: dog name`);
        equal(attributes(dog), attributes(source), `${stage}: attributes`);
    }
    equal(target.blocks.length, original.blocks.length, `${stage}: block count`);
    for (const source of original.blocks) {
        const block = target.blocks.find(b => b.x === source.x && b.y === source.y && b.z === source.z);
        assert.ok(block, `${stage}: block missing at ${source.x}`);
        for (const [key, value] of Object.entries(source)) {
            if (['name', 'ownerName'].includes(key)) equal(component(block[key]), component(value), `${stage}: block ${key}`);
            else equal(block[key], value, `${stage}: block ${source.x} ${key}`);
        }
    }
    const locations = data(target, 'dimensions/minecraft/overworld/data/doggytalents/dog_locations_dtn.dat', 'locationData');
    const sourceLocations = data(original, 'data/doggytalentsDogLocations.dat', 'locationData');
    equal(locations.length, sourceLocations.length, `${stage}: indexed dogs including unloaded dog`);
    for (const source of sourceLocations) {
        const found = locations.find(d => JSON.stringify(d.uuid) === JSON.stringify(source.uuid));
        assert.ok(found, `${stage}: offline location absent`);
        for (const [key, value] of Object.entries(source)) {
            equal(key === 'name_text_component' ? component(found[key]) : found[key],
                key === 'name_text_component' ? component(value) : value, `${stage}: location ${key}`);
        }
    }
    const respawns = data(target, 'dimensions/minecraft/overworld/data/doggytalents/dead_dogs.dat', 'respawnData');
    equal(respawns, data(original, 'data/doggytalentsDeadDogs.dat', 'respawnData'), `${stage}: complete revival payload`);
    for (const oldFile of ['data/doggytalentsDogLocations.dat', 'data/doggytalentsDeadDogs.dat']) {
        equal(target.savedData[oldFile], original.savedData[oldFile], `${stage}: original index retained`);
    }
}
console.log(JSON.stringify({test_id: 'REVIEW-UPGRADE-01', result: 'PASS', assertions,
    dogs: original.dogs.length, blockEntities: original.blocks.length,
    stages: ['original 1.21.1 save', 'production 26.1.2 upgrade', 'production 26.1.2 restart']}, null, 2));
