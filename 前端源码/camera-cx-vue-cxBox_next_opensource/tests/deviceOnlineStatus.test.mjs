// Synthetic acceptance branch only; no device, network, or full-page validation.
import test from 'node:test';
import assert from 'node:assert/strict';
import { existsSync, readFileSync } from 'node:fs';
import vm from 'node:vm';
const helperUrl = new URL('../src/utils/deviceOnlineStatus.mjs', import.meta.url);
async function status(value) {
  assert.ok(existsSync(helperUrl), 'device status helper must distinguish unknown values');
  const { deviceOnlineStatus } = await import(helperUrl.href);
  return deviceOnlineStatus(value);
}
test('explicit numeric and string online values are green', async () => {
  for (const value of [1, '1']) assert.deepEqual(await status(value), { state: 'online', type: 'success' });
});
test('explicit numeric and string offline values are red', async () => {
  for (const value of [0, '0']) assert.deepEqual(await status(value), { state: 'offline', type: 'danger' });
});
test('missing and unrecognized values stay unknown rather than offline', async () => {
  for (const value of [null, undefined, '', ' ', '01', ' 1 ', true, false, 2, -1, NaN, [], {}, ['1']]) {
    assert.deepEqual(await status(value), { state: 'unknown', type: 'info' });
  }
});
test('unknown objects are not coerced or executed', async () => {
  assert.deepEqual(await status({ valueOf() { throw Error('must not coerce'); } }), { state: 'unknown', type: 'info' });
});
test('existing device list method displays unknown and retains online/offline translations', async () => {
  const source = readFileSync(new URL('../src/views/applicationMonitoring/GBManagement/deviceList.vue', import.meta.url), 'utf8');
  const script = source.match(/<script>([\s\S]*?)<\/script>/)[1];
  const executable = script.replace(/^import .*;\s*$/gm, '').replace('export default', 'globalThis.component =');
  const sandbox = { Tables: {}, ChannelManagement: {}, ConfigInfo: {}, DeviceForm: {},
    deviceOnlineStatus: async () => {}, $t: key => key };
  if (existsSync(helperUrl)) sandbox.deviceOnlineStatus = (await import(helperUrl.href)).deviceOnlineStatus;
  vm.runInNewContext(executable, sandbox);
  const method = sandbox.component.methods.onlineStatus;
  assert.equal(typeof method, 'function', 'device list must use the three-state presentation');
  const ctx = { $t: key => key };
  assert.equal(method.call(ctx, null).label, '状态未知');
  assert.equal(method.call(ctx, '1').label, 'common.online');
  assert.equal(method.call(ctx, 0).label, 'common.offline');
  assert.equal(method.call(ctx, undefined).type, 'info');
});
