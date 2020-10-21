package com.AYC.canalguide

import android.util.Log
import com.AYC.canalguide.data.xml_classes.LiftBridges
import com.tickaroo.tikxml.TikXml
import okio.BufferedSource
import okio.Okio
import okio.buffer
import okio.source
import org.junit.Test

import org.junit.Assert.*
import java.io.InputStream

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {



    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun xml_parse_test() {
        val xml = """<?xml version="1.0" encoding="utf-8"?>
<liftbridges>
    <liftbridge latitude="42.65447" longitude="-73.74208" name="Amtrak (Livingston Ave)*" location="Albany " mile="146.38" bodyofwater="Hudson River" phonenumber="(518) 465-0746" clearance_closed="25'" clearance_opened="135'" />
    <liftbridge latitude="42.73547" longitude="-73.68967" name="Green Island*" location="Troy " mile="152.74" bodyofwater="Hudson River" phonenumber="(518) 724-7586" clearance_closed="29'" clearance_opened="60'" />
    <liftbridge latitude="43.10160" longitude="-77.44192" name="Main Street [E-128]" location="Fairport " mile="246.67" bodyofwater="Erie Canal" phonenumber="(585) 223-9412" clearance_closed="7'" clearance_opened="16.3'" />
    <liftbridge latitude="43.19335" longitude="-77.80038" name="Union Street [E-174]" location="Spencerport" mile="271.28" bodyofwater="Erie Canal" phonenumber="(585) 352-5451" clearance_closed="3'" clearance_opened="17.0'" />
    <liftbridge latitude="43.19578" longitude="-77.85384" name="Washington St [E-178]" location="Adams Basin" mile="274.21" bodyofwater="Erie Canal" phonenumber="(585) 352-3548" clearance_closed="3'" clearance_opened="16.9'" />
    <liftbridge latitude="43.21536" longitude="-77.93537" name="Park Avenue [E-181]" location="Brockport" mile="278.76" bodyofwater="Erie Canal" phonenumber="(585) 637-0460" clearance_closed="3'" clearance_opened="16.9'" />
    <liftbridge latitude="43.21669" longitude="-77.93815" name="N Main St [E-182]" location="Brockport" mile="278.93" bodyofwater="Erie Canal" phonenumber="(585) 637-4530" clearance_closed="3'" clearance_opened="16.7'" />
    <liftbridge latitude="43.22860" longitude="-78.02166" name="East Avenue [E-187]" location="Holley" mile="283.48" bodyofwater="Erie Canal" phonenumber="(585) 638-6456" clearance_closed="3'" clearance_opened="16.7'" />
    <liftbridge latitude="43.25438" longitude="-78.06624" name="Hulberton Road [E-191]" location="Hulberton" mile="286.58" bodyofwater="Erie Canal" phonenumber="(585) 638-8183" clearance_closed="3'" clearance_opened="16.7'" />
    <liftbridge latitude="43.24853" longitude="-78.19042" name="Ingersoll Street [E-199]" location="Albion" mile="292.98" bodyofwater="Erie Canal" phonenumber="(585) 589-4107" clearance_closed="3'" clearance_opened="16.5'" />
    <liftbridge latitude="43.24907" longitude="-78.19367" name="North Main Street [E-200]" location="Albion" mile="293.15" bodyofwater="Erie Canal" phonenumber="(585) 589-6255" clearance_closed="3'" clearance_opened="16.5'" />
    <liftbridge latitude="43.25186" longitude="-78.25314" name="Eagle Harbor Rd [E-203]" location="Eagle Hbr" mile="296.41" bodyofwater="Erie Canal" phonenumber="(585) 589-0628" clearance_closed="3'" clearance_opened="16.5'" />
    <liftbridge latitude="43.24264" longitude="-78.31063" name="Knowlesville Rd [E-206]" location="Knowlesville" mile="299.47" bodyofwater="Erie Canal" phonenumber="(585) 798-2050" clearance_closed="3'" clearance_opened="16.5'" />
    <liftbridge latitude="43.22547" longitude="-78.39176" name="Prospect Avenue [E-211]" location="Medina" mile="304.13" bodyofwater="Erie Canal" phonenumber="(585) 798-0140" clearance_closed="3'" clearance_opened="16.5'" />
    <liftbridge latitude="43.21289" longitude="-78.47644" name="Main Street [E-216]" location="Middleport" mile="308.87" bodyofwater="Erie Canal" phonenumber="(716) 735-7250" clearance_closed="3'" clearance_opened="16.6'" />
    <liftbridge latitude="43.19961" longitude="-78.57584" name="Gasport Road [E-222]" location="Gasport" mile="314.15" bodyofwater="Erie Canal" phonenumber="(716) 772-7700" clearance_closed="3'" clearance_opened="16.6'" />
    <liftbridge latitude="43.17867" longitude="-78.68278" name="Adam Street [E-229]" location="Lockport" mile="319.92" bodyofwater="Erie Canal" phonenumber="N/A, kept open" clearance_closed="3'" clearance_opened="16.6'" />
    <liftbridge latitude="43.17675" longitude="-78.68566" name="Exchange Street [E-230]" location="Lockport" mile="320.12" bodyofwater="Erie Canal" phonenumber="(716) 434-7368" clearance_closed="3'" clearance_opened="16.6'" />
    <liftbridge latitude="42.93134" longitude="-78.90218" name="International Railroad*" location="Buffalo" mile="8.61" bodyofwater="Niagara River" phonenumber="(716) 876-5670" clearance_closed="17'" clearance_opened="infinite" />
    <liftbridge latitude="42.91519" longitude="-78.90225" name="Ferry Street*" location="Buffalo" mile="9.77" bodyofwater="Niagara River" phonenumber="(716) 851-5689" clearance_closed="17'" clearance_opened="infinite" />
</liftbridges>"""


        val parser = TikXml.Builder()
            .exceptionOnUnreadXml(false) // CRTL + F on this site for more info: https://github.com/Tickaroo/tikxml/blob/master/docs/AnnotatingModelClasses.md
            //.writeDefaultXmlDeclaration(false)
            .build()

        val bufferedXml = xml.byteInputStream().source().buffer()

        val obj = parser.read<LiftBridges>(bufferedXml, LiftBridges::class.java)

        println("obj count = ${obj.liftBridges.count()}")
        println("obj[0]  = ${obj.liftBridges[0]}")

        assertNotNull(obj)
    }

}
