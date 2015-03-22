using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    enum EString
    {
        Sixth,
        Fifth,
        Fourth,
        Third,
        Second,
        First,

        FirstString = Sixth,
        LastString = First,

        NumberOfStrings,
    }

    enum ENotes
    {
        A,
        ASharp,
        BBemol = ASharp,
        B,
        CBemol = B,
        C,
        CSharp,
        DBemol = CSharp,
        D,
        DSharp,
        EBemol = DSharp,
        E,
        FBemol = E,
        F,
        FSharp,
        GBemol = FSharp,
        G,
        GSharp,
        ABemol = GSharp,

        NumberOfNotes,
    }

    class Globals
    {
        // Should be more dynamic
        public static CFingerPosition s_posFirstGuitarPosition =
            new CFingerPosition(EString.Sixth, 0);

        // Should be more dynamic
        public static CNoteFingerPosition s_posFirstGuitarNote =
            new CNoteFingerPosition(s_posFirstGuitarPosition, (int)ENotes.E);

        public const int NUMBER_OF_FRETS = 24;

        public const int NUMBER_OF_FRETS_IN_OCTAVE = 12;

    }
}
